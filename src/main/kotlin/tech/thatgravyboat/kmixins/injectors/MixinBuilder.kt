package tech.thatgravyboat.kmixins.injectors

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec

class MixinBuilder {

    private val methods: MutableList<MethodSpec.Builder> = mutableListOf()
    private val fields: MutableList<FieldSpec.Builder> = mutableListOf()

    fun method(name: String): MethodSpec.Builder = MethodSpec.methodBuilder(name).also(methods::add)
    fun field(name: String, type: Class<*>): FieldSpec.Builder = FieldSpec.builder(type, name).also(fields::add)

    fun buildMethods(): List<MethodSpec> = methods.map(MethodSpec.Builder::build)
    fun buildFields(): List<FieldSpec> = fields.map(FieldSpec.Builder::build)
}