package silentorb.mythic.aura.generation

import java.nio.ByteBuffer

fun getPaddedLength(value: Any): Int =
    when (value) {
        is ByteArray -> 4 + getPaddedByteArrayLength(value.size)
        is Int -> 4
        is Float -> 4
        is Double -> 8
        is Long -> 8
        is String -> 4 + getPaddedStringLength(value.length)
        else -> throw Error("Unsupported type to serialize: ${value.javaClass.name}")
    }

fun getFieldTypePrefix(value: Any): Char =
    when (value) {
        is ByteArray -> 'b'
        is Int -> 'i'
        is Float -> 'f'
        is Double -> 'd'
        is Long -> 't'
        is String -> 's'
        else -> throw Error("Unsupported type to serialize: ${value.javaClass.name}")
    }

fun getArgumentsLength(arguments: List<Argument>): Int =
    if (arguments.none())
        0
    else {
        val tagsLength = arguments.size * 4
        tagsLength + arguments.sumBy { getPaddedLength(it) }
    }

fun writeArgument(buffer: ByteBuffer, value: Argument) {
    when (value) {
        is Int -> {
            buffer.putInt(value)
        }
        is Float -> {
            buffer.putFloat(value)
        }
        is Double -> {
            buffer.putDouble(value)
        }
        is Long -> {
            buffer.putLong(value)
        }
        is ByteArray -> {
            buffer.putInt(value.size)
            buffer.put(value)
            padBytes(buffer, getBasePaddingLength(value.size))
        }
        is String -> {
            writeString(buffer, value)
        }
        else -> throw Error("Unsupported type to serialize: ${value.javaClass.name}")
    }
}

fun serializeMessage(message: Message): ByteArray {
    val command = message.command
    val arguments = message.arguments
    val length = getPaddedStringLength(command.length) + getArgumentsLength(arguments)
    val result = ByteArray(length)
    val buffer = ByteBuffer.wrap(result)
    writeString(buffer, command)
    if (arguments.any()) {
        buffer.put(','.toByte())
        for ((index, argument) in arguments.withIndex()) {
            val prefix = getFieldTypePrefix(argument).toByte()
            buffer.put(prefix)
            buffer.put(0)
            buffer.put(0)
            if (index > 0) {
                buffer.put(0)
            }
            writeArgument(buffer, argument)
        }
    }

    return result
}
