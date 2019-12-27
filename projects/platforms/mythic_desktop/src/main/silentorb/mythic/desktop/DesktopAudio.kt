package silentorb.mythic.desktop

import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.openal.ALCCapabilities
import org.lwjgl.openal.ALCapabilities
import org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.libc.LibCStdlib.free
import silentorb.mythic.platforming.LoadSoundResult
import silentorb.mythic.platforming.PlatformAudio

fun loadSoundFromFile(filename: String): LoadSoundResult {
  // Allocate space to store return information from the function
  stackPush()
  val channelsBuffer = stackMallocInt(1)
  stackPush()
  val sampleRateBuffer = stackMallocInt(1)

  val inputBuffer = stb_vorbis_decode_filename(filename, channelsBuffer, sampleRateBuffer)

  // Retreive the extra information that was stored in the buffers by the function
  val channels = channelsBuffer.get()
  val sampleRate = sampleRateBuffer.get()

  // Free the space we allocated earlier
  stackPop()
  stackPop()

  assert(inputBuffer != null)
  val format = when (channels) {
    1 -> AL_FORMAT_MONO16
    2 -> AL_FORMAT_STEREO16
    else -> throw Error("Invalid channel count for audio file $filename: $channels")
  }
  val duration = inputBuffer.limit().toFloat() / channels.toFloat() / sampleRate.toFloat()
  val buffer: Int = alGenBuffers()
  alBufferData(buffer, format, inputBuffer, sampleRate)

  free(inputBuffer)
  return LoadSoundResult(
      buffer = buffer,
      duration = duration
  )
}

fun millisecondsToBytes(milliseconds: Int): Int {
  val sampleRate = 44100
  val channels = 2
  val byteDepth = 2
  return milliseconds * sampleRate * channels * byteDepth / 1000
}

class DesktopAudio : PlatformAudio {
  var device: Long = 0L
  var context: Long = 0L
  val buffers: MutableSet<Int> = mutableSetOf()
  val sources: MutableSet<Int> = mutableSetOf()

  override fun start(latency: Int) {
    val defaultDeviceName: String = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER)
    device = alcOpenDevice(defaultDeviceName)

    val attributes = intArrayOf(0)
    context = alcCreateContext(device, attributes)
    alcMakeContextCurrent(context)

    val alcCapabilities: ALCCapabilities = ALC.createCapabilities(device)
    val alCapabilities: ALCapabilities = AL.createCapabilities(alcCapabilities)
  }

  override fun play(buffer: Int, volume: Float, x: Float, y: Float, z: Float): Int {
    val source = alGenSources()
    alSourcei(source, AL_BUFFER, buffer)
    alSourcePlay(source)
    return source
  }

  override fun playingSounds(): Set<Int> =
      sources

  override fun update() {
    val finished = sources.filter { source ->
      alGetSourcei(source, AL_SOURCE_STATE) == AL_STOPPED
    }
    finished.forEach(::alDeleteSources)
    sources.removeAll(sources)
  }

  override fun stop() {
    sources.forEach(::alDeleteSources)
    sources.clear()
    buffers.forEach(::alDeleteBuffers)
    buffers.clear()
    alcDestroyContext(context)
    alcCloseDevice(device)
    device = 0
  }

  override fun loadSound(filename: String): LoadSoundResult {
    val result = loadSoundFromFile(filename)
    buffers.add(result.buffer)
    return result
  }
}
