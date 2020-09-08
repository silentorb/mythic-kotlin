package silentorb.mythic.desktop

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load_from_memory
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import silentorb.mythic.platforming.*
import silentorb.mythic.resource_loading.ioResourceToByteBuffer
import silentorb.mythic.spatial.Vector2i
import java.nio.ByteBuffer

fun createWindow(title: String, config: PlatformDisplayConfig): Long {
//  val pid = ManagementFactory.getRuntimeMXBean().getName()
//  println("pid: " + pid)
  glfwDefaultWindowHints() // optional, the current window hints are already the default
  glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
  glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable
  glfwWindowHint(GLFW_SAMPLES, config.multisamples)
//  val pid = ProcessHandle.current().getPid()

  val window = glfwCreateWindow(config.dimensions.x, config.dimensions.y, title, MemoryUtil.NULL, MemoryUtil.NULL)
  if (window == MemoryUtil.NULL)
    throw RuntimeException("Failed to create the GLFW window")

  return window
}

fun createHeadlessWindow(): Long {
  glfwDefaultWindowHints()
  glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

  val window = glfwCreateWindow(320, 200, "", MemoryUtil.NULL, MemoryUtil.NULL)
  if (window == MemoryUtil.NULL)
    throw RuntimeException("Failed to create the GLFW window")

  glfwMakeContextCurrent(window)
  return window
}

fun centerWindow(window: Long) {
  MemoryStack.stackPush().use { stack ->
    val width = stack.mallocInt(1)
    val height = stack.mallocInt(1)

    glfwGetWindowSize(window, width, height)

    val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())

    glfwSetWindowPos(
        window,
        (videoMode.width() - width.get()) / 2,
        (videoMode.height() - height.get()) / 2
    )
  }
}

fun initializeWindowedFullscreen(window: Long) {
  val monitor = glfwGetPrimaryMonitor()
  val videoMode = glfwGetVideoMode(monitor)
  glfwSetWindowMonitor(window, monitor, 0, 0, videoMode.width(), videoMode.height(), videoMode.refreshRate())
  glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
  glfwFocusWindow(window) // For some reason the window loses focus when switching to fullscreen mode?
}

fun initializeWindowed(window: Long, dimensions: Vector2i) {
  glfwSetWindowSize(window, dimensions.x, dimensions.y)
  centerWindow(window)
}

fun initializeWindow(window: Long, config: PlatformDisplayConfig) {
  when (config.windowMode) {
    WindowMode.fullscreen -> initializeWindowedFullscreen(window) // Currently only supporting windowed fullscreen
    WindowMode.windowedFullscreen -> initializeWindowedFullscreen(window)
    else -> initializeWindowed(window, config.dimensions)
  }

  glfwMakeContextCurrent(window)

  // Enable v-sync
  glfwSwapInterval(if (config.vsync) 1 else 0)

  glfwShowWindow(window)
}

fun getWindowInfo(window: Long): WindowInfo {
  MemoryStack.stackPush().use { stack ->
    val width = stack.mallocInt(1)
    val height = stack.mallocInt(1)
    glfwGetWindowSize(window, width, height)

    return WindowInfo(Vector2i(width.get(), height.get()))
  }
}

val loadImageFromFile: ImageLoader = { filePath ->
  var buffer: ByteBuffer? = null
  var width = 0
  var height = 0
  var channels = 0
  MemoryStack.stackPush().use { stack ->
    val widthBuffer = stack.mallocInt(1)
    val heightBuffer = stack.mallocInt(1)
    val channelBuffer = stack.mallocInt(1)

    val imageBuffer = ioResourceToByteBuffer(filePath)

//    stbi_set_flip_vertically_on_load(true)
    buffer = stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, channelBuffer, 0)
    if (buffer == null) {
      val reason = stbi_failure_reason()
      throw RuntimeException("Failed to load a texture file!"
          + System.lineSeparator() + reason)
    }

    width = widthBuffer.get()
    height = heightBuffer.get()
    channels = channelBuffer.get()
  }

  if (buffer != null)
    RawImage(
        buffer = buffer!!,
        width = width,
        height = height,
        channels = channels
    )
  else
    null
}

class DesktopDisplay(val window: Long) : PlatformDisplay {
  override fun initialize(config: PlatformDisplayConfig) = initializeWindow(window, config)

  override fun getInfo(): WindowInfo = getWindowInfo(window)

  override fun swapBuffers() = glfwSwapBuffers(window)

  override fun hasFocus() = glfwGetWindowAttrib(window, GLFW_FOCUSED) == 1

  override val loadImage: ImageLoader
    get() = loadImageFromFile
}
