package silentorb.mythic.aura.generation

import java.nio.ByteBuffer

fun getBasePaddingLength(contentLength: Int): Int {
    val extra = contentLength % 4
    return 4 - extra
}

fun getStringPaddingLength(length: Int): Int {
    val padding = getBasePaddingLength(length)
    return if (padding == 0)
        4
    else
        padding
}

fun getPaddedStringLength(length: Int): Int =
    length + getStringPaddingLength(length)

fun getPaddedByteArrayLength(length: Int): Int =
    length + getBasePaddingLength(length)

fun padBytes(output: ByteBuffer, padding: Int) {
    for (i in (0 until padding)) {
        output.put(0)
    }
}

fun writeString(buffer: ByteBuffer, value: String) {
    val padding = getStringPaddingLength(value.length)
    buffer.put(value.toByteArray())
    padBytes(buffer, padding)
}
