package club.redux.sunset.hrtffix.api

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility

interface IConstants {
    fun getClass(): KClass<*> = this::class
}

fun <T : IConstants> T.toMap(): Map<String, String> {
    return this.getClass().members
        .filter { it is KProperty<*> && it.visibility == KVisibility.PUBLIC }
        .associate { it.name.lowercase() to (it.call().toString()) }
}