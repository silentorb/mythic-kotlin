package silentorb.mythic.debugging

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private var dotEnv: Dotenv? = null

private var privateLoopNumber = 0

fun setGlobalLoopNumber(value: Int) {
  privateLoopNumber = value
}

fun incrementGlobalDebugLoopNumber(max: Int) {
  privateLoopNumber = ++privateLoopNumber % max
}

private var debugRangeValue = 0f
private var lastModified: Long = 0L

fun getDebugRangeValue(): Float = debugRangeValue

fun setDebugRangeValue(value: Float) {
  debugRangeValue = value
}

private var debugString = ""

fun getDebugString(): String = debugString

fun setDebugString(value: String) {
  debugString = value
}

tailrec fun findParentDotEnvFile(path: Path = Paths.get(System.getProperty("user.dir"))): String? =
    when {
      Files.exists(path.resolve(".env")) -> path.toString()
      path.parent == null -> null
      else -> findParentDotEnvFile(path.parent)
    }

fun getDotEnvDirectory(): String =
    System.getenv("DOTENV_DIRECTORY") ?: findParentDotEnvFile() ?: ""

fun newDotEnv() = dotenv {
  directory = getDotEnvDirectory()
  ignoreIfMissing = true
}

fun reloadDotEnv() {
  dotEnv = newDotEnv()
}

fun checkDotEnvChanged() {
  val dotEnvDirectory = getDotEnvDirectory()
  val modified = File(Paths.get(dotEnvDirectory, ".env").toUri()).lastModified()
  if (modified > lastModified) {
    lastModified = modified
    reloadDotEnv()
    println("Detected .env changes and reloaded ${java.util.Date()}")
  }
}

fun getDebugString(name: String): String? {
  dotEnv = dotEnv
      ?: newDotEnv()
  return dotEnv!![name]
}

fun getConfigString(name: String): String? =
    getDebugString(name)

fun getDebugInt(name: String): Int? =
    getDebugString(name)?.toIntOrNull()

fun getDebugLong(name: String): Long? =
    getDebugString(name)?.toLongOrNull()

fun getDebugBoolean(name: String): Boolean =
    getDebugInt(name)?.equals(1) ?: false

fun getDebugFloat(name: String): Float? =
    getDebugString(name)?.toFloatOrNull()

fun debugLog(message: String) {
  println("($privateLoopNumber) $message")
}

fun conditionalDebugLog(booleanSetting: String): (() -> String) -> Unit = { message ->
  if (getDebugBoolean(booleanSetting)) {
    println(message())
  }
}
