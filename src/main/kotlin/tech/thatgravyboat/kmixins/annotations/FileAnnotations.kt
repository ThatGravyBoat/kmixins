package tech.thatgravyboat.kmixins.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class KMixin(
    vararg val value: KClass<*>,
    val priority: Int = 1000,
    val remap: Boolean = true,
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class KPseudoMixin(
    vararg val value: String,
    val priority: Int = 1000,
    val remap: Boolean = true,
)