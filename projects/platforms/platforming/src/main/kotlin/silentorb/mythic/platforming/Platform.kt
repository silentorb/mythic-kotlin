package silentorb.mythic.platforming

import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector3
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ShortBuffer
import java.nio.file.Path

data class WindowInfo(val dimensions: Vector2i)

data class InputEvent(
    val device: Int,
    val index: Int,
    val value: Float
)

const val keyboardDeviceIndex = 0
const val mouseDeviceIndex = 1
const val generalGamepadDeviceIndex = 2

interface PlatformInput {
  fun update()
  fun getMousePosition(): Vector2
  fun isMouseVisible(value: Boolean)
  fun getEvents(): List<InputEvent>
}

enum class WindowMode {
  fullscreen,
  windowed,
  windowedFullscreen
}

data class PlatformDisplayConfig(
    val dimensions: Vector2i,
    val windowMode: WindowMode = WindowMode.windowed,
    val vsync: Boolean,
    val multisamples: Int
)

data class RawImage(
    val buffer: ByteBuffer,
    val width: Int,
    val height: Int,
    val channels: Int
)

typealias ImageLoader = (String) -> RawImage?

interface PlatformDisplay {
  fun initialize(config: PlatformDisplayConfig)
  fun swapBuffers()
  fun getInfo(): WindowInfo
  fun hasFocus(): Boolean
  val loadImage: ImageLoader
}

data class LoadSoundResult(
    val buffer: Int,
    val duration: Float
)

interface PlatformAudio {
  fun start(latency: Int)
  fun play(buffer: Int, volume: Float, position: Vector3?): Int
  fun playingSounds(): Set<Int>
  fun update(listenerPosition: Vector3?)
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
}

data class Platform(
    val audio: PlatformAudio,
    val display: PlatformDisplay,
    val input: PlatformInput,
    val process: PlatformProcess
)
