package silentorb.mythic.desktop

import silentorb.mythic.platforming.PlatformDisplayConfig
import silentorb.mythic.platforming.Platform
import silentorb.mythic.platforming.PlatformProcess
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

fun initializeDesktopPlatform() {
  if (!glfwInit())
    throw Error("Unable to initialize GLFW")
}

fun createDesktopPlatform(title: String, config: PlatformDisplayConfig): Platform {
  GLFWErrorCallback.createPrint(System.err).set()
  initializeDesktopPlatform()

  val window = createWindow(title, config)
  return Platform(
      audio = DesktopAudio(),
      display = DesktopDisplay(window),
      input = DesktopInput(window),
      process = DesktopProcess(window)
  )
}
