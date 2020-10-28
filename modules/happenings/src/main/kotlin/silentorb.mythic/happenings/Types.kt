package silentorb.mythic.happenings

data class Command(
    val type: Any,
    val target: Long = 0,
    val value: Float = 0f,
    val device: Int = 0
)

typealias Commands = List<Command>

typealias CommandName = String
