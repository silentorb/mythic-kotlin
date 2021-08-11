package silentorb.mythic.desktop

import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.openal.ALCCapabilities
import org.lwjgl.openal.ALCapabilities
import org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.libc.LibCStdlib.free
import silentorb.mythic.platforming.LoadSoundResult
import silentorb.mythic.platforming.PlatformAudio
import silentorb.mythic.resource_loading.ioResourceToByteBuffer
import silentorb.mythic.spatial.Vector3
import java.nio.ShortBuffer

fun loadSoundFromBuffer(inputBuffer: ShortBuffer, channels: Int, sampleRate: Int): LoadSoundResult {
  val format = when (channels) {
    1 -> AL_FORMAT_MONO16
    2 -> AL_FORMAT_STEREO16
    else -> throw Error("Invalid channel count: $channels")
  }
  val duration = inputBuffer.limit().toFloat() / channels.toFloat() / sampleRate.toFloat()
  val buffer: Int = alGenBuffers()
  alBufferData(buffer, format, inputBuffer, sampleRate)

  return LoadSoundResult(
      buffer = buffer,
      duration = duration
  )
}

fun loadSoundFromFile(filePath: String): LoadSoundResult {
  // Allocate space to store return information from the function
  stackPush()
  val channelsBuffer = stackMallocInt(1)
  stackPush()
  val sampleRateBuffer = stackMallocInt(1)

  val inputBuffer = ioResourceToByteBuffer(filePath)
  val audioBuffer = stb_vorbis_decode_memory(inputBuffer, channelsBuffer, sampleRateBuffer)

  // Retrieve the extra information that was stored in the buffers by the function
  val channels = channelsBuffer.get()
  val sampleRate = sampleRateBuffer.get()

  // Free the space we allocated earlier
  stackPop()
  stackPop()

  assert(audioBuffer != null)
  when (channels) {
    1 -> AL_FORMAT_MONO16
    2 -> AL_FORMAT_STEREO16
    else -> throw Error("Invalid channel count for audio file $filePath: $channels")
  }
  val result = loadSoundFromBuffer(audioBuffer!!, channels, sampleRate)
  free(audioBuffer)
  return result
}

fun millisecondsToBytes(milliseconds: Int): Int {
  val sampleRate = 44100
  val channels = 2
  val byteDepth = 2
  return milliseconds * sampleRate * channels * byteDepth / 1000
}

class DesktopAudio : PlatformAudio {
  val sourceCount = 16
  var device: Long = 0L
  var context: Long = 0L
  val buffers: MutableSet<Int> = mutableSetOf()
  var sources: ArrayList<Int> = arrayListOf()
  val sourcesBusy: BooleanArray = BooleanArray(sourceCount) { false }
  var gain: Float = 1f

  override val isActive: Boolean get() = device != 0L

  override fun start(latency: Int) {
    val defaultDeviceName: String = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER)!!
    device = alcOpenDevice(defaultDeviceName)

    if (device != 0L) {
      val attributes = intArrayOf(0)
      context = alcCreateContext(device, attributes)
      alcMakeContextCurrent(context)

      val alcCapabilities: ALCCapabilities = ALC.createCapabilities(device)
      val alCapabilities: ALCapabilities = AL.createCapabilities(alcCapabilities)
      val sourceBuffer = IntArray(sourceCount)
      alGenSources(sourceBuffer)
      sources.addAll(sourceBuffer.toTypedArray())
    }
  }

  override fun play(buffer: Int, volume: Float, position: Vector3?): Int? {
    val sourceIndex = sourcesBusy.indexOf(false)
    return if (sourceIndex == -1)
      null
    else {
      val source = sources[sourceIndex]
      sourcesBusy[sourceIndex] = true
      alSourcei(source, AL_BUFFER, buffer)
      if (position != null) {
        alSource3f(source, AL_POSITION, position.x, position.y, position.z)
        alSourcef(source, AL_GAIN, volume)
      }
      alSourcePlay(source)
      source
    }
  }

  override fun getPlayingSounds(): Collection<Int> =
      sources

  override fun update(gain: Float, listenerPosition: Vector3?) {
    if (gain != this.gain) {
      this.gain = gain
      alListenerf(AL_GAIN, gain)
    }

    if (listenerPosition != null) {
      alListener3f(AL_POSITION, listenerPosition.x, listenerPosition.y, listenerPosition.z)
    }

    sources.forEachIndexed { index, source ->
      if (alGetSourcei(source, AL_SOURCE_STATE) == AL_STOPPED) {
        sourcesBusy[index] = false
      }
    }
    val error = alGetError()
    if (error != 0) {
      val k = 0
    }
  }

  override fun unloadAllSounds() {
    sources.forEach(::alDeleteSources)
    sources.clear()
    buffers.forEach(::alDeleteBuffers)
    buffers.clear()
  }

  override fun stop() {
    if (isActive) {
      unloadAllSounds()
      alcDestroyContext(context)
      alcCloseDevice(device)
      device = 0
    }
  }

  override fun loadSound(filename: String): LoadSoundResult {
    val result = loadSoundFromFile(filename)
    buffers.add(result.buffer)
    return result
  }

  override fun loadSound(buffer: ShortBuffer, channels: Int, sampleRate: Int): LoadSoundResult {
    val result = loadSoundFromBuffer(buffer, channels, sampleRate)
    buffers.add(result.buffer)
    return result
  }

  override fun unloadSound(buffer: Int) {
    if (buffers.contains(buffer)) {
      buffers.remove(buffer)
      alDeleteBuffers(buffer)
    }
  }
}
