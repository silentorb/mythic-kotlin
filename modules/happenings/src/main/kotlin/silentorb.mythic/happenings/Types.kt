package silentorb.mythic.happenings

data class Command(
    val type: Any,
    val value: Any? = null,
    val target: Any? = null,
    val device: Int = 0
)

typealias Commands = List<Command>

typealias CommandName = String
