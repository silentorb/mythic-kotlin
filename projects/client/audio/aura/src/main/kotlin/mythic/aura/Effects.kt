package mythic.aura

import mythic.spatial.Vector3

val maxSoundRange = 40f

fun toDb(value: Float): Float {
  val base = 10
  val result = (Math.pow(base.toDouble(), value.toDouble()) - 1) / (base - 1)
  return result.toFloat()
}

fun distanceAttenuation(listenerPosition: Vector3?, soundPosition: Vector3?): Float =
    if (soundPosition != null) {
      if (listenerPosition == null)
        0f
      else {
        val distance = listenerPosition.distance(soundPosition) - 2f
        if (distance < 0f)
          1f
        else
          toDb(1f - Math.min(1f, distance / maxSoundRange))
      }
    } else
      1f

private const val cutoff: Int = (Short.MAX_VALUE * 0.7f).toInt()

fun applyCutoff(value: Int): Int =
    cutoff + (value - cutoff) / 8

val compress: (Int) -> Int = { value ->
  when {
    value > cutoff -> applyCutoff(value)
    value < -cutoff -> -applyCutoff(-value)
    else -> value
  }
}

val clip: (Int) -> Int = { value ->
  when {
    value > Short.MAX_VALUE -> Short.MAX_VALUE.toInt()
    value < Short.MIN_VALUE -> Short.MIN_VALUE.toInt()
    else -> value
  }
}
