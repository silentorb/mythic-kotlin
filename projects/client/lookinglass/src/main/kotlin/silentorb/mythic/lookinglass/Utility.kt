package silentorb.mythic.lookinglass

// Even though numbers have no case, still include them in the regex
// so hypens or underscores followed by a number are converted
private val camelCaseRegex = Regex("[-_][a-z0-9]")

fun toCamelCase(identifier: String) =
    identifier.replace(camelCaseRegex) { it.value[1].toUpperCase().toString() }
