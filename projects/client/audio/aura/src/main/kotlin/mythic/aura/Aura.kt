package mythic.aura

import mythic.ent.WithId
import mythic.ent.Id
import mythic.ent.Table
import mythic.ent.pipe2
import mythic.platforming.PlatformAudio
import mythic.spatial.Vector3
import java.nio.ByteBuffer
import java.nio.ShortBuffer

data class SoundData(
   override val id: Id,
    val buffer: ShortBuffer,
    val duration: Long
) : WithId

data class Sound(
    override val id: Id,
    val type: Id,
    val progress: Long = 0L,
    val position: Vector3? = null
) : WithId

data class BufferState(
    val maxSize: Int,
    val bufferedBytes: Int
)

fun newBufferState(audio: PlatformAudio) =
    BufferState(
        maxSize = audio.availableBuffer,
        bufferedBytes = 0
    )

typealias SoundTable = Table<Sound>
typealias SoundLibrary = Table<SoundData>

data class AudioState(
    val sounds: SoundTable,
    val buffer: BufferState,
    val nextSoundId: Id = 0L,
    val volume: Float
)

fun newAudioState(audio: PlatformAudio, volume: Float) =
    AudioState(
        sounds = mapOf(),
        buffer = newBufferState(audio),
        volume = volume
    )

data class CalculatedSound(
    val remainingSamples: Int,
    val progress: Int,
    val instrument: (Int) -> Short,
    val gain: Float
)

fun prepareSounds(sounds: SoundTable, library: SoundLibrary, listenerPosition: Vector3?): List<CalculatedSound> =
    sounds.values
        .map { sound ->
          val info = library[sound.type]!!
          val gain = distanceAttenuation(listenerPosition, sound.position)

          CalculatedSound(
              remainingSamples = (info.duration - sound.progress).toInt(),
              progress = sound.progress.toInt(),
              instrument = { info.buffer.get(it) },
              gain = gain
          )
        }
        .filter { it.gain > 0f }

private const val useSineTest: Boolean = false

fun renderAudio(library: SoundLibrary, samples: Int, listenerPosition: Vector3?, sounds: SoundTable, buffer: ByteBuffer) {
  val activeSounds = if (useSineTest)
    sineTest(listenerPosition)
  else
    prepareSounds(sounds, library, listenerPosition)

  (0 until samples).forEach { i ->
    val value: Int = activeSounds
        .filter { i < it.remainingSamples }
        .map { (it.instrument(it.progress + i) * it.gain).toInt() }
        .sum()

    val finalValue = pipe2(value, listOf(
        compress,
        clip
    ))

    buffer.putShort(finalValue.toShort())
    buffer.putShort(finalValue.toShort())
  }

  if (useSineTest)
    updateSineTest(samples)
}

fun renderSilence(samples: Int, buffer: ByteBuffer) {
  (0 until samples).forEach { i ->
    buffer.putShort(0.toShort())
    buffer.putShort(0.toShort())
  }
}

fun updateSounds(library: SoundLibrary, samples: Int): (SoundTable) -> SoundTable = { sounds ->
  pipe2(sounds, listOf(
      { s ->
        s.mapValues { (_, sound) ->
          sound.copy(
              progress = sound.progress + samples
          )
        }
      },
      { s ->
        s.filterValues { sound ->
          val info = library[sound.type]!!
          sound.progress < info.duration
        }
      }
  ))
}

fun getBufferSize(samples: Int): Int {
  val bytesPerSample = 2 * 2
  return bytesPerSample * samples
}

fun updateSounds(audio: PlatformAudio, library: SoundLibrary, samples: Int, listenerPosition: Vector3?, volume: Float): (SoundTable) -> SoundTable = { sounds ->
  val bufferSize = getBufferSize(samples)
  val buffer = getMixBuffer(bufferSize)
  if (volume == 0f) {
    renderSilence(samples, buffer)
  } else {
    renderAudio(library, samples, listenerPosition, sounds, buffer)
  }
  updateAudioDeviceBuffer(audio, buffer, bufferSize)
  updateSounds(library, samples)(sounds)
}
