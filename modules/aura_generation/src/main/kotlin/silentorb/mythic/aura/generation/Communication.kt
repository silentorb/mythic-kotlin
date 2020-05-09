package silentorb.mythic.aura.generation

import java.io.DataOutputStream
import java.net.Socket

fun post(socket: Socket, messages: List<ByteArray>) {
    val output = DataOutputStream(socket.getOutputStream())
    val input = socket.getInputStream()
    if (messages.none())
        return

    val isBundle = messages.size > 1
    val additionalLength = if (isBundle) 4 else 0
    val length = 8 + additionalLength + messages.sumBy { it.size } + messages.size * 4
    output.writeInt(length)
    output.writeBytes("#bundle")
    output.writeByte(0)
    if (isBundle) {
        output.writeLong(1)
    }

    for (message in messages) {
        output.writeInt(message.size)
        output.write(message)
    }

    output.flush()

    val start = System.currentTimeMillis()
    val end = start + 1000L * 1L
    println("Waiting for response...")
    while (input.available() > 0 || System.currentTimeMillis() < end) {
        if (input.available() > 0) {
            println("available: ${input.available()} ${end - System.currentTimeMillis()}")
            val buffer = input.readNBytes(input.available())
            println(String(buffer))
        }
        Thread.sleep(1)
    }
}

//fun post(socket: Socket, message: Message) {
//    post(socket, serializeMessage(message))
//}

fun sendMessages(messages: List<Message>) {
    val arrays = messages.map(::serializeMessage)
    Socket("127.0.0.1", 57110).use { socket ->
        socket.setSoTimeout(4000)
        for (array in arrays) {
            post(socket, listOf(array))
        }
//        post(socket, arrays)
    }
}
