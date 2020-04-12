package silentorb.mythic.debugging

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

private var dotEnv: Dotenv? = null

private var privateLoopNumber = 0

fun setGlobalLoopNumber(value: Int) {
  privateLoopNumber = value
}

fun incrementGlobalDebugLoopNumber(max: Int) {
  privateLoopNumber = ++privateLoopNumber % max
}

private var debugRangeValue = 0f

fun getDebugRangeValue(): Float = debugRangeValue

fun setDebugRangeValue(value: Float) {
  debugRangeValue = value
}

fun newDotEnv() = dotenv {
  directory = System.getenv("DOTENV_DIRECTORY") ?: ""
  ignoreIfMissing = true
}

fun getDebugString(name: String): String? {
  dotEnv = dotEnv
      ?: newDotEnv()
  return dotEnv!![name]
}

fun getDebugInt(name: String): Int? =
    getDebugString(name)?.toIntOrNull()

fun getDebugBoolean(name: String): Boolean =
    getDebugInt(name)?.equals(1) ?: false

fun getDebugFloat(name: String): Float? =
    getDebugString(name)?.toFloatOrNull()

fun isDebugSet(name: String): Boolean {
  val value = getDebugString(name)
  return value == "1"
}

fun debugLog(message: String) {
  println("($privateLoopNumber) $message")
}
