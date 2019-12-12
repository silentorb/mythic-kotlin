package mythic.desktop

import mythic.platforming.PlatformDisplayConfig
import mythic.platforming.Platform
import mythic.platforming.PlatformProcess
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFWErrorCallback

fun is64Bit(): Boolean {
  if (System.getProperty("os.name").contains("Windows")) {
    return System.getenv("ProgramFiles(x86)") != null;
  } else {
    return System.getProperty("os.arch").indexOf("64") != -1;
  }
}

class DesktopProcess(val window: Long) : PlatformProcess {
  override fun close() {
    GLFW.glfwSetWindowShouldClose(window, true)
  }

  override fun pollEvents() {
    glfwPollEvents()
  }

  override fun isClosing(): Boolean = GLFW.glfwWindowShouldClose(window)

  override fun shutdownPlatform() {
    glfwTerminate()
  }
}

fun createDesktopPlatform(title: String, config: PlatformDisplayConfig): Platform {
  GLFWErrorCallback.createPrint(System.err).set()
  if (!glfwInit())
    throw Error("Unable to initialize GLFW")

  val window = createWindow(title, config)
  return Platform(
      audio = DesktopAudio(),
      display = DesktopDisplay(window),
      input = DesktopInput(window),
      process = DesktopProcess(window)
  )
}
