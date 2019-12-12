package mythic.aura

import mythic.spatial.Vector3

private var kz = 0L

fun sineTest(listenerPosition: Vector3?) = listOf(
    CalculatedSound(
        remainingSamples = 1000000,
        progress = kz.toInt(),
        instrument = { (Math.sin((kz + it).toDouble() * 0.1) * Short.MAX_VALUE).toShort() },
        gain = distanceAttenuation(listenerPosition, mythic.spatial.globalPosition)
    )
)

fun updateSineTest(samples: Int) {
  kz += samples
}