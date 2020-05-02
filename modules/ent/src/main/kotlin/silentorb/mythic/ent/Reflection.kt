package silentorb.mythic.ent

import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> reflectProperties(source: Any): List<T> = source::class.java.kotlin.memberProperties
    .filter {
      it.returnType.classifier == T::class
    }
    .map { it.call(source) as T }

inline fun <reified T : Any> reflectPropertiesMap(source: Any): Map<String, T> = source::class.java.kotlin.members
    .filter {
      it.returnType.classifier == T::class
    }
    .associate { Pair(it.name, it.call(source) as T) }
