package silentorb.mythic.aura.generation

enum class FieldType {
    bytes,
    int,
    float,
    double,
    long,
    string,
    timestamp
}

data class Argument(
    val type: FieldType,
    val content: ByteArray
)

data class Message(
    val command: String,
    val arguments: List<Argument>
)
