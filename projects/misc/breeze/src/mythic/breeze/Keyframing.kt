package mythic.breeze

import mythic.spatial.Quaternion
import mythic.spatial.Vector3
import mythic.spatial.Vector3m
import mythic.spatial.Vector4

data class Keyframe(
    val time: Float,
    val value: Any
)

data class AnimationChannel(
    val target: String,
    val keys: Keyframes
)

typealias Keyframes = List<Keyframe>

inline fun getCurrentKeys(keys: List<Keyframe>, timePassed: Float): Pair<Keyframe, Keyframe?> {
  for (i in 0 until keys.size) {
    val key = keys[i]
    if (key.time > timePassed) {
      return Pair(keys[i - 1], key)
    }
  }

  return Pair(keys.last(), null)
}

fun getProgress(first: Float, second: Float, timePassed: Float): Float {
  val duration = second - first
  val localSecondsPassed = timePassed - first
  return localSecondsPassed / duration
}

typealias Interpolator = (Any, Any, Float) -> Any

typealias TypedInterpolator<T> = (T, T, Float) -> T

val interpolateVector3: TypedInterpolator<Vector3> = { a, b, progress ->
  Vector3(Vector3m(a).lerp(Vector3m(b), progress))
}

val interpolateVector4: TypedInterpolator<Vector4> = { a, b, progress ->
  Vector4(a).lerp(Vector4(b), progress)
}

val interpolateQuaternion: TypedInterpolator<Quaternion> = { a, b, progress ->
  Quaternion(a).slerp(b, progress)
}

fun <T>interpolateKeys(keys: Keyframes, timePassed: Float, interpolate: TypedInterpolator<T>): T {
  val (firstKey, secondKey) = getCurrentKeys(keys, timePassed)
  return if (secondKey == null) {
    firstKey.value as T
  } else {
    val progress = getProgress(firstKey.time, secondKey.time, timePassed)
    interpolate(firstKey.value as T, secondKey.value as T, progress)
  }
}

fun getStandardChannelValue(keys: Keyframes, timePassed: Float): Any =
    interpolateKeys(keys, timePassed) { first, second, progress ->
      val interpolate = when (first) {
        is Vector3 -> interpolateVector3
        is Quaternion -> interpolateQuaternion
        else -> throw Error("Not implemented.")
      } as Interpolator
      interpolate(first, second, progress)
    }
