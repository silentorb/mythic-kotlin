package silentorb.mythic.aura.generation

import java.io.DataOutputStream
import java.net.Socket

fun writeMessage(output: DataOutputStream, data: ByteArray) {
    output.writeInt(data.size)
    output.write(data)
}

fun post(socket: Socket, data: ByteArray) {
    val output = DataOutputStream(socket.getOutputStream())
    val input = socket.getInputStream()
    writeMessage(output, data)
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

fun post(socket: Socket, message: Message) {
    post(socket, serializeMessage(message))
}

fun sendMessages(messages: List<Message>) {
    val arrays = messages.map(::serializeMessage)
    Socket("127.0.0.1", 57110).use { socket ->
        socket.setSoTimeout(4000)
        for (array in arrays) {
            post(socket, array)
        }
    }
}
