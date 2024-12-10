package tech.thatgravyboat.kmixins.annotations

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import tech.thatgravyboat.kmixins.utils.getAnnotation
import tech.thatgravyboat.kmixins.utils.get
import tech.thatgravyboat.kmixins.utils.hasAnnotation
import tech.thatgravyboat.kmixins.utils.toJava

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class KShadow(
    val kind: ShadowKind = ShadowKind.FIELD
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class KSelf

enum class ShadowKind {
    FIELD,
    METHOD,
    FINAL_FIELD,
}

internal fun KSAnnotated.hasSpecialAnnotations(): Boolean {
    return this.hasAnnotation<KShadow>() || this.hasAnnotation<KSelf>()
}

internal fun KSFunctionDeclaration.createMixinParameters(): String =
    this.parameters.joinToString(", ") {
        when {
            it.hasAnnotation<KSelf>() -> "(${it.type.toJava()}) (Object) this"
            it.hasAnnotation<KShadow>() -> when (it.getAnnotation<KShadow>().get<ShadowKind>("kind")) {
                ShadowKind.FIELD, ShadowKind.FINAL_FIELD -> it.name!!.asString()
                ShadowKind.METHOD -> "this::${it.name!!.asString()}"
            }
            else -> it.name!!.asString()
        }
    }