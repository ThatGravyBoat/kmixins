package tech.thatgravyboat.kmixins.injectors.builtin

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.TypeName
import com.squareup.kotlinpoet.asClassName
import org.objectweb.asm.Opcodes
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect
import tech.thatgravyboat.kmixins.annotations.KStatic
import tech.thatgravyboat.kmixins.annotations.createMixinParameters
import tech.thatgravyboat.kmixins.annotations.hasSpecialAnnotations
import tech.thatgravyboat.kmixins.injectors.InjectionSerializer
import tech.thatgravyboat.kmixins.injectors.MixinBuilder
import tech.thatgravyboat.kmixins.utils.*
import javax.lang.model.element.Modifier

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class KRedirectField(
    val method: String,
    val target: String,
    val ordinal: Int = -1,
    val remap: Boolean = true,
)

class RedirectFieldSerializer : InjectionSerializer {

    private fun KSFunctionDeclaration.isVoid() = this.returnType!!.isType(TypeName.VOID)

    override fun readAnnotation(function: KSFunctionDeclaration, annotation: KSAnnotation) = with(annotation) {
        val opcode = if (function.isVoid()) Opcodes.PUTFIELD else Opcodes.GETFIELD
        AnnotationSpec.builder(Redirect::class.java)
            .addProperty("method", get<String>("method"))
            .addAnnotation("at", At::class.asClassName()) {
                add("value", "FIELD")
                add("target", get<String>("target"))
                add("ordinal", get<Int>("ordinal"))
                add("opcode", opcode)
            }
            .addProperty("remap", get<Boolean>("remap"))
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
            .returns(function.returnType!!.toJava())

        function.parameters
            .filter { !it.hasSpecialAnnotations() }
            .forEach {
                require(!it.hasDefault) { "Parameters with default values are not supported in injected methods" }
                method.addParameter(it)
            }

        if (function.isVoid()) {
            method.addStatement(
                "\$T.INSTANCE.${function.simpleName.asString()}(${function.createMixinParameters()})",
                klass.toJava()
            )
        } else {
            method.addStatement(
                "return \$T.INSTANCE.${function.simpleName.asString()}(${function.createMixinParameters()})",
                klass.toJava()
            )
        }

        //TODO get shadows
    }
}