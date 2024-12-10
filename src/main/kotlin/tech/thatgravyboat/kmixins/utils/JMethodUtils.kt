package tech.thatgravyboat.kmixins.utils

import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.javapoet.MethodSpec
import javax.lang.model.element.Modifier

internal fun MethodSpec.Builder.addModifiers(add: Boolean, vararg modifiers: Modifier): MethodSpec.Builder =
    if (add) this.addModifiers(*modifiers) else this

internal fun MethodSpec.Builder.addParameter(parameter: KSValueParameter): MethodSpec.Builder =
    this.addParameter(parameter.type.toJava(), parameter.name!!.asString())