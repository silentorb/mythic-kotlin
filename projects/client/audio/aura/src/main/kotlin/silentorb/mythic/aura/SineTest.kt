package silentorb.mythic.aura

import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.globalPosition

private var kz = 0L

fun sineTest(listenerPosition: Vector3?) = listOf(
    CalculatedSound(
        remainingSamples = 1000000,
        progress = kz.toInt(),
        instrument = { (Math.sin((kz + it).toDouble() * 0.1) * Short.MAX_VALUE).toShort() },
        gain = distanceAttenuation(listenerPosition, globalPosition)
    )
)

fun updateSineTest(samples: Int) {
  kz += samples
}
