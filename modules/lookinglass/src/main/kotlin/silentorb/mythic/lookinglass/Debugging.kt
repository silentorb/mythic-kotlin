package silentorb.mythic.lookinglass

import org.lwjgl.opengl.GL43.*
import org.lwjgl.opengl.GLDebugMessageCallback
import org.lwjgl.system.APIUtil
import org.lwjgl.system.MemoryUtil
import java.io.PrintStream

fun printDetail(stream: PrintStream, type: String, message: String) {
  stream.printf("\t%s: %s\n", type, message)
}

fun getGlDebugSource(source: Int): String =
    when (source) {
      GL_DEBUG_SOURCE_API -> "API"
      GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "WINDOW SYSTEM"
      GL_DEBUG_SOURCE_SHADER_COMPILER -> "SHADER COMPILER"
      GL_DEBUG_SOURCE_THIRD_PARTY -> "THIRD PARTY"
      GL_DEBUG_SOURCE_APPLICATION -> "APPLICATION"
      GL_DEBUG_SOURCE_OTHER -> "OTHER"
      else -> APIUtil.apiUnknownToken(source)
    }

fun getGlDebugType(type: Int): String =
    when (type) {
      GL_DEBUG_TYPE_ERROR -> "ERROR"
      GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "DEPRECATED BEHAVIOR"
      GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "UNDEFINED BEHAVIOR"
      GL_DEBUG_TYPE_PORTABILITY -> "PORTABILITY"
      GL_DEBUG_TYPE_PERFORMANCE -> "PERFORMANCE"
      GL_DEBUG_TYPE_OTHER -> "OTHER"
      GL_DEBUG_TYPE_MARKER -> "MARKER"
      else -> APIUtil.apiUnknownToken(type)
    }

fun getDebugSeverity(severity: Int): String =
    when (severity) {
      GL_DEBUG_SEVERITY_HIGH -> "HIGH"
      GL_DEBUG_SEVERITY_MEDIUM -> "MEDIUM"
      GL_DEBUG_SEVERITY_LOW -> "LOW"
      GL_DEBUG_SEVERITY_NOTIFICATION -> "NOTIFICATION"
      else -> APIUtil.apiUnknownToken(severity)
    }

fun logGlMessage(source: Int, type: Int, id: Int, severity: Int, length: Int, message: Long, userParam: Long) {
  println("[LWJGL] OpenGL debug message")
  printDetail(APIUtil.DEBUG_STREAM, "ID", String.format("0x%X", id))
  printDetail(APIUtil.DEBUG_STREAM, "Source", getGlDebugSource(source))
  printDetail(APIUtil.DEBUG_STREAM, "Type", getGlDebugType(type))
  printDetail(APIUtil.DEBUG_STREAM, "Severity", getDebugSeverity(severity))
  printDetail(APIUtil.DEBUG_STREAM, "Message", GLDebugMessageCallback.getMessage(length, message))
}

fun registerGlDebugLogging() {
  val proc = GLDebugMessageCallback.create { source: Int, type: Int, id: Int, severity: Int, length: Int, message: Long, userParam: Long ->
    if (severity != GL_DEBUG_SEVERITY_NOTIFICATION)
      logGlMessage(source, type, id, severity, length, message, userParam)
  }
  glDebugMessageCallback(proc, MemoryUtil.NULL)
}
