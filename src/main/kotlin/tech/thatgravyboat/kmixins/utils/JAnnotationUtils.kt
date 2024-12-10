package tech.thatgravyboat.kmixins.utils

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.squareup.javapoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName

internal inline fun <reified T> KSAnnotated.getAnnotation(): KSAnnotation = this.annotations.first {
    it.annotationType.resolve().declaration.qualifiedName!!.asString() == T::class.qualifiedName
}

internal inline fun <reified T : Annotation> KSAnnotated.hasAnnotation(): Boolean {
    @OptIn(KspExperimental::class)
    return this.isAnnotationPresent(T::class)
}

internal inline fun <reified T> KSAnnotation.get(id: String): T {
    val value = this.arguments.first { it.name?.asString() == id }.value
    if (T::class.java.isEnum) {
        val enum = (value as KSType).declaration.simpleName.asString()
        return T::class.java.enumConstants.first { (it as Enum<*>).name == enum } as T
    }
    return value as T
}

internal fun AnnotationSpec.Builder.addProperty(name: String, value: Any): AnnotationSpec.Builder = when (value) {
    is String -> this.addMember(name, "\$S", value)
    is Enum<*> -> this.addMember(name, "\$T.\$L", value::class.asClassName(), value.name)
    else -> this.addMember(name, "\$L", value)
}

internal fun AnnotationSpec.Builder.addProperty(
    add: Boolean,
    name: String,
    value: Any,
): AnnotationSpec.Builder = if (add) this.addProperty(name, value) else this

internal fun AnnotationSpec.Builder.addAnnotation(
    name: String,
    klass: ClassName,
    props: PropertyBuilder.() -> Unit
): AnnotationSpec.Builder {
    val propsMap = PropertyBuilder().also(props)

    return this.addMember(
        name,
        "@\$T(${propsMap.entries.joinToString { (k, v) -> "$k = ${v.first}" }})",
        klass,
        *propsMap.values.flatMap { it.second }.toTypedArray()
    )
}

internal class PropertyBuilder : LinkedHashMap<String, Pair<String, List<Any?>>>() {

    fun add(name: String, value: Any?) {
        when (value) {
            is String -> add(name, "\$S", value)
            is Enum<*> -> add(name, "\$T.\$L", value::class.asClassName(), value.name)
            else -> add(name, "\$L", value)
        }
    }

    fun add(name: String, format: String, vararg value: Any?) {
        this[name] = format to value.asList()
    }
}