@file:OptIn(KotlinPoetJavaPoetPreview::class)

package tech.thatgravyboat.kmixins.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.TypeName
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toJClassName
import com.squareup.kotlinpoet.javapoet.toJTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName


internal fun KSType.toJava(): TypeName = when {
    this.toTypeName() == UNIT -> TypeName.VOID
    this.toTypeName() == ARRAY -> ArrayTypeName.of(this.arguments.first().type!!.toJava())
    else -> this.toTypeName().toJTypeName()
}

internal fun KSTypeReference.isType(type: TypeName) = this.toJava() == type

internal fun KSTypeReference.toJava() = this.resolve().toJava()

internal fun KSClassDeclaration.toJava(): TypeName = when (this.toClassName()) {
    UNIT -> TypeName.VOID
    else -> this.toClassName().toJClassName()
}