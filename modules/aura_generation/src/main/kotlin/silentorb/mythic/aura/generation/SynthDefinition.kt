package silentorb.mythic.aura.generation

import java.nio.ByteBuffer

data class Parameter(
    val default: Float
)

data class Input(
    val unitGenerator: Int,
    val specifier: Int
)

data class UnitGenerator(
    val className: String,
    val calculationRate: CalculationRate,
    val inputs: List<Input>,
    val outputs: List<CalculationRate>,
    val specialIndex: Int = 0
)

data class SynthDefinition(
    val name: String,
    val constants: List<Float>,
    val parameters: List<Parameter>,
    val parameterNames: List<String>,
    val unitGenerators: List<UnitGenerator>
)

fun serializeString(buffer: ByteBuffer, text: String) {
    buffer.put(text.length.toByte())
    buffer.put(text.toByteArray())
}

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
        serializeString(buffer, name)
        buffer.putInt(1) // TODO: "its index in the parameter array"
    }

    buffer.putInt(definition.unitGenerators.size)
    for (unitGenerator in definition.unitGenerators) {
        serializeString(buffer, unitGenerator.className)
        buffer.put(unitGenerator.calculationRate.value.toByte())
        buffer.putInt(unitGenerator.inputs.size)
        buffer.putInt(unitGenerator.outputs.size)
        buffer.putShort(unitGenerator.specialIndex.toShort())
        for (input in unitGenerator.inputs) {
            buffer.putInt(input.unitGenerator)
            buffer.putInt(input.specifier)
        }
        for (output in unitGenerator.outputs) {
            buffer.put(output.value.toByte())
        }
    }
    val variantCount = 0.toShort()
    buffer.putShort(variantCount)
}

fun serializeSynthDefinitions(buffer: ByteBuffer, definitions: List<SynthDefinition>) {
    serializeSynthDefinitionsHeader(buffer, definitions)
    for (definition in definitions) {
        serializeSynthDefinition(buffer, definition)
    }
}

fun serializeSynthDefinitions(definitions: List<SynthDefinition>): ByteArray {
    val buffer = ByteBuffer.allocate(2048)
    serializeSynthDefinitions(buffer, definitions)
    val byteArray = ByteArray(buffer.position())
    buffer.rewind()
    buffer.get(byteArray)
    return byteArray
}


