package silentorb.mythic.ent

import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> reflectProperties(source: Any): List<T> =
    source::class.java.kotlin.memberProperties
        .filter {
          it.returnType.classifier == T::class
        }
        .map { it.call(source) as T }

inline fun <reified T : Any> reflectPropertiesMap(source: Any): Map<String, T> =
    source::class.java.kotlin.members
        .filter {
          it.returnType.classifier == T::class
        }
        .associate { Pair(it.name, it.call(source) as T) }

fun getProperty(source: Any, property: String): KProperty<*>? =
    source::class.java.kotlin.memberProperties
        .firstOrNull { it.name == property }

inline fun <reified T : Any> getPropertyValue(source: Any, property: String): T? =
    getProperty(source, property)?.call(source) as? T

inline fun <reified T : Any> setPropertyValue(source: Any, property: String, value: T) =
    getProperty(source, property)?.call(source, value)

inline fun <reified T : Any> copyDataClass(source: T, values: Map<String, Any>): T {
  val constructor = source::class.java.kotlin.constructors.first()
  val arguments = constructor.parameters.map { parameter ->
    values[parameter.name] ?: getPropertyValue(source, parameter.name!!)
  }
  return constructor.call(*arguments.toTypedArray())
}
