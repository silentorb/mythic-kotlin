package silentorb.mythic.platforming

import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector3
import java.nio.ByteBuffer
import java.nio.ShortBuffer

data class WindowInfo(val dimensions: Vector2i, val id: Long)

data class InputEvent(
    val device: Int,
    val index: Int,
    val value: Float
)

object Devices {
  const val keyboard = 0
  const val mouse = 1
  const val gamepadFirst = 2
}

interface PlatformInput {
  fun update()
  fun getMousePosition(): Vector2
  fun setMouseVisibility(value: Boolean)
  fun getEvents(): List<InputEvent>
  fun getKeyName(key: Int): String?
}

enum class WindowMode {
  windowed,
  fullscreen,
  windowedFullscreen
}

data class PlatformDisplayConfig(
    val fullscreenDimensions: Vector2i,
    val windowedDimensions: Vector2i?,
    val windowMode: WindowMode = WindowMode.windowed,
    val vsync: Boolean,
    val multisamples: Int
)

data class ImageInfo(
    val width: Int,
    val height: Int,
    val channels: Int
)

data class RawImage(
    val buffer: ByteBuffer,
    val info: ImageInfo,
)

typealias ImageLoader = (String) -> RawImage?
typealias ImageInfoLoader = (String) -> ImageInfo

data class DisplayMode(
    val resolution: Vector2i
)

interface PlatformDisplay {
  fun initialize(config: PlatformDisplayConfig)
  fun swapBuffers()
  fun getInfo(): WindowInfo
  fun hasFocus(): Boolean
  fun setOptions(previous: PlatformDisplayConfig, options: PlatformDisplayConfig)
  fun getDisplayModes(): List<DisplayMode>
  fun shutdown()

  val loadImage: ImageLoader
  val loadImageInfo: ImageInfoLoader
}

data class LoadSoundResult(
    val buffer: Int,
    val duration: Float
)

interface PlatformAudio {
  val isActive: Boolean
  fun start(latency: Int)
  fun play(buffer: Int, volume: Float, position: Vector3?): Int?
  fun getPlayingSounds(): Collection<Int>
  fun update(gain: Float, listenerPosition: Vector3?)
  fun loadSound(filename: String): LoadSoundResult
  fun loadSound(buffer: ShortBuffer, channels: Int, sampleRate: Int): LoadSoundResult
  fun unloadAllSounds()
  fun unloadSound(buffer: Int)
  fun stop()
}

interface PlatformProcess {
  fun close()
  fun isClosing(): Boolean
  fun pollEvents()
  fun shutdownPlatform()
  fun messageBox(title: String, message: String)
}

data class Platform(
    val audio: PlatformAudio,
    val display: PlatformDisplay,
    val input: PlatformInput,
    val process: PlatformProcess
)
