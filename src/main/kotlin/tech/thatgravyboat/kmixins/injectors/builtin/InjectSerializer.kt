package tech.thatgravyboat.kmixins.injectors.builtin

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.javapoet.AnnotationSpec
import com.squareup.kotlinpoet.asClassName
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.LocalCapture
import tech.thatgravyboat.kmixins.annotations.KStatic
import tech.thatgravyboat.kmixins.annotations.createMixinParameters
import tech.thatgravyboat.kmixins.annotations.hasSpecialAnnotations
import tech.thatgravyboat.kmixins.constants.InjectionKind
import tech.thatgravyboat.kmixins.injectors.InjectionSerializer
import tech.thatgravyboat.kmixins.injectors.MixinBuilder
import tech.thatgravyboat.kmixins.utils.*
import tech.thatgravyboat.kmixins.utils.addAnnotation
import tech.thatgravyboat.kmixins.utils.get
import javax.lang.model.element.Modifier

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class KInject(
    val method: String,
    val kind: InjectionKind,
    val cancellable: Boolean = false,
    val captureLocals: Boolean = false,
    val remap: Boolean = true,
)

class InjectSerializer : InjectionSerializer {

    override fun readAnnotation(function: KSFunctionDeclaration, annotation: KSAnnotation) = with(annotation) {
        AnnotationSpec.builder(Inject::class.java)
            .addProperty("method", get<String>("method"))
            .addAnnotation("at", At::class.asClassName()) {
                add("value", get<InjectionKind>("kind").name)
            }
            .addProperty("cancellable", get<Boolean>("cancellable"))
            .addProperty("remap", get<Boolean>("remap"))
            .addProperty(get<Boolean>("captureLocals"), "locals", LocalCapture.CAPTURE_FAILHARD)
            .build()
    }

    override fun write(
        klass: KSClassDeclaration,
        annotation: AnnotationSpec,
        function: KSFunctionDeclaration,
        builder: MixinBuilder
    ) {
        val method = builder.method(function.simpleName.asString())
            .addModifiers(Modifier.PRIVATE)
            .addModifiers(function.hasAnnotation<KStatic>(), Modifier.STATIC)
            .addAnnotation(annotation)
            .returns(Void.TYPE)

        function.parameters
            .filter { !it.hasSpecialAnnotations() }
            .forEach {
                require(!it.hasDefault) { "Parameters with default values are not supported in injected methods" }
                method.addParameter(it)
            }

        method.addStatement(
            "\$T.INSTANCE.${function.simpleName.asString()}(${function.createMixinParameters()})",
            klass.toJava()
        )

        //TODO get shadows
    }
}