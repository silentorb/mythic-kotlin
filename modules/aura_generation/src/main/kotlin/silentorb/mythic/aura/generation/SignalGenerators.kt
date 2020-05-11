package silentorb.mythic.aura.generation

import kotlin.math.sin

fun sine(position: Float): Float =
    sin(position)

fun saw(position: Float): Float =
    position - 1

fun square(position: Float): Float =
    if (position < 0.5f) 1f else -1f

fun squarePulseWidth(position: Float, pulse_width: Float): Float {
  val half = (1f - pulse_width) * 0.5f
  val value = position % 1f
  return if (value > half && value < 1f - half) 1f else -1f
}

fun triangle(x: Float): Float {
  val a = x * 4f
  return if (x < 0.25f)
    a
  else if (x < 0.75f)
    2f - a
  else
    a - 4f
}
