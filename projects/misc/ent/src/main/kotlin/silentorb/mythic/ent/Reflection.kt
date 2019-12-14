package silentorb.mythic.ent

inline fun <reified T : Any> reflectProperties(source: Any): List<T> = source::class.java.kotlin.members
    .filter {
      it.returnType.classifier == T::class }
    .map { it.call(source) as T }
