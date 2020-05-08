package silentorb.mythic.aura.generation

import java.nio.ByteBuffer

data class Parameter(
    val default: Float
)

data class Input(
    val unitGenerator: Int,
    val data: Int
)

data class UnitGenerator(
    val className: String,
    val calculationRate: Int,
    val inputs: List<Input>,
    val outputs: List<Int>
)

data class SynthDefinition(
    val name: String,
    val constants: List<Float>,
    val parameters: List<Parameter>,
    val parameterNames: List<String>,
    val unitGenerators: List<UnitGenerator>
)

fun serializeSynthDefinitionsHeader(buffer: ByteBuffer, definitions: List<SynthDefinition>) {
    val header = "SCgf".toByteArray()
    buffer.put(header)
    buffer.putInt(2)
    buffer.putShort(definitions.size.toShort())
}

fun serializeSynthDefinition(buffer: ByteBuffer, definition: SynthDefinition) {
    buffer.putInt(definition.constants.size)
    for (constant in definition.constants) {
        buffer.putFloat(constant)
    }

    buffer.putInt(definition.parameters.size)
    for (parameter in definition.parameters) {
        buffer.putFloat(parameter.default)
    }

    buffer.putInt(definition.parameterNames.size)
    for (name in definition.parameterNames) {
        buffer.put(name.length.toByte())
        buffer.put(name.toByteArray())
        buffer.putInt(1) // TODO: "its index in the parameter array"
    }
}

fun serializeSynthDefinitions(buffer: ByteBuffer, definitions: List<SynthDefinition>) {
    serializeSynthDefinitionsHeader(buffer, definitions)
    for (definition in definitions) {
        serializeSynthDefinition(buffer, definition)
    }
}

fun serializeSynthDefinitions(definitions: List<SynthDefinition>): ByteBuffer {
    val buffer = ByteBuffer.allocate(2048)
    serializeSynthDefinitions(buffer, definitions)
    return buffer
}


