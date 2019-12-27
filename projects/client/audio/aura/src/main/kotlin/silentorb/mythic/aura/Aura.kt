package silentorb.mythic.aura

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.platforming.PlatformAudio
import silentorb.mythic.spatial.Vector3

typealias SoundBuffer = Int

typealias SoundType = String
typealias SoundDurations = Map<SoundType, Float>
data class SoundData(
    val type: SoundType,
    val buffer: SoundBuffer,
    val duration: Float
)

typealias SoundMap = Map<Id, SoundBuffer>

data class Sound(
    val type: SoundType,
    val volume: Float,
    val progress: Float,
    val position: Vector3? = null
)

typealias SoundLibrary = Map<SoundType, SoundData>

data class AudioState(
    val sounds: SoundMap,
    val volume: Float
)

fun newAudioState(volume: Float) =
    AudioState(
        sounds = mapOf(),
        volume = volume
    )

data class CalculatedSound(
    val remainingSamples: Int,
    val progress: Int,
    val instrument: (Int) -> Short,
    val gain: Float
)

fun updateSoundPlaying(audio: PlatformAudio, newSounds: Table<Sound>, library: SoundLibrary, listenerPosition: Vector3?, volume: Float): (SoundMap) -> SoundMap = { soundMap ->
  audio.update(listenerPosition)
  val newSoundMappings = newSounds.mapValues { (_, sound) ->
    val definition = library[sound.type]!!
    val position = sound.position ?: Vector3.zero
    audio.play(definition.buffer, sound.volume, position)
  }
  val playingSounds = audio.playingSounds()
  soundMap
      .filterValues { playingSounds.contains(it) }
      .plus(newSoundMappings)
}

fun updateSound(delta: Float): (Sound) -> Sound = { sound ->
  sound.copy(
      progress = sound.progress + delta
  )
}

fun finishedSounds(soundDurations: SoundDurations): (Table<Sound>) -> Set<Id> = { sounds ->
  sounds
      .filterValues { it.progress >= soundDurations[it.type]!! }
      .keys
}
