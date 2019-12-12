package mythic.spatial

fun quadOut(t: Float, b: Float, c: Float, d: Float): Float {
  val t2 = t / d
  return -c * t2 * (t2 - 2) + b
}

fun quadOut(t: Float): Float {
  return t * (2 - t)
}

fun quadIn(t: Float): Float = t * t

fun cubicIn(t: Float): Float = t * t * t

fun cubicOut(t: Float): Float {
  val n = t - 1
  return n * n * n + 1
}

fun quadInOut(t: Float): Float {
  val n = t * 2;
  return if (t < 0.5f)
    0.5f * n * n
//  t
  else {
    val j = n - 1
    -0.5f * (j * (j - 2) - 1)
    t
  }
}

typealias EasingFunction = (Float) -> Float


fun invertCurve(curve: EasingFunction): EasingFunction = { t ->
  val n = 1 - t
  1 - curve(n)
}

fun interpolate(scalar: Float, a: Vector3, b: Vector3): Vector3 =
    b * scalar + a * (1f - scalar)

fun interpolate(scalar: Float, a: Quaternion, b: Quaternion): Quaternion =
    Quaternion(a).slerp(b, scalar)