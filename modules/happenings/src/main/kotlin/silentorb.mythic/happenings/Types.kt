package silentorb.mythic.happenings

data class Command(
    val type: Any,
    val value: Any = 0f,
    val target: Long = 0,
    val device: Int = 0
)

typealias Commands = List<Command>

typealias CommandName = String
