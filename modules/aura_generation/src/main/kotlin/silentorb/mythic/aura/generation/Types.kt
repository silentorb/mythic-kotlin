package silentorb.mythic.aura.generation

enum class FieldType {
    bytes,
    int,
    float,
    double,
    string,
    longTimestamp
}

typealias Argument = Any

data class Message(
    val command: String,
    val arguments: List<Argument>
)

enum class CalculationRate(val value: Int) {
    scalar(0),
    control(1),
    audio(2)
}
