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

fun getDebugSetting(name: String): String? {
  dotEnv = dotEnv
      ?: newDotEnv()
  return dotEnv!![name]
}

fun debugLog(message: String) {
  println("($privateLoopNumber) $message")
}
