package tech.thatgravyboat.kmixins.injectors

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.javapoet.AnnotationSpec

interface InjectionSerializer {

    fun readAnnotation(
        function: KSFunctionDeclaration,
        annotation: KSAnnotation,
    ): AnnotationSpec

    fun write(
        klass: KSClassDeclaration,
        annotation: AnnotationSpec,
        function: KSFunctionDeclaration,
        builder: MixinBuilder,
    )
}