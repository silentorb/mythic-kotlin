package silentorb.mythic.aura.generation

import java.io.DataOutputStream
import java.net.Socket

fun getArgumentLength(arguments: List<Argument>): Int =
    if (arguments.none())
        0
    else
        1 + arguments.sumBy { it.content.size + 1 }

fun getPaddedStringLength(text: String): Int {
    val extra = text.length % 4
    val padding = 4 - extra
    return text.length + padding
}

fun writeMessage(output: DataOutputStream, message: Message) {
    val command = message.command
    val commandLength = getPaddedStringLength(command)
    val length = commandLength + getArgumentLength(message.arguments)
    output.writeInt(length)
    output.writeBytes(command)
    for (i in (0 until (commandLength - command.length))) {
        output.writeByte(0)
    }
}

fun post(socket: Socket, message: Message) {
    val output = DataOutputStream(socket.getOutputStream())
    val input = socket.getInputStream()
    writeMessage(output, message)
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

fun sendSound() {
    Socket("127.0.0.1", 57110).use { socket ->
        socket.setSoTimeout(4000)
        post(socket, Message(Commands.version, listOf()))
    }
}
