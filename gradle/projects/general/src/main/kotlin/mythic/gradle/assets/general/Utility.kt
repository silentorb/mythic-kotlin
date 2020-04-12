package mythic.gradle.assets.general

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

fun printStream(inputStream: InputStream) {
  val reader = BufferedReader(InputStreamReader(inputStream))
  var line: String?
  while (reader.readLine().also { line = it } != null) {
    println(line)
  }
}

fun runProcess(args: List<String>) {
  println(args.joinToString(" "))
  val processBuilder = ProcessBuilder(args)
  processBuilder.redirectErrorStream(true)
  val process = processBuilder.start()
  printStream(process.inputStream)
  if (!process.waitFor(60, TimeUnit.SECONDS)) {
    process.destroy()
    throw RuntimeException("Execution timed out")
  }
  printStream(process.inputStream)
  if (process.exitValue() != 0) {
    throw RuntimeException("Execution failed with code ${process.exitValue()}")
  }
}
