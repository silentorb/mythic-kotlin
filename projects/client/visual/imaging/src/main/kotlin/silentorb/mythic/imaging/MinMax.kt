package silentorb.mythic.imaging

var minValue: Float = 0f
var maxValue: Float = 0f

fun resetMinMax() {
  minValue = 0f;
  maxValue = 0f
}

fun printMinMax() {
  println("min/max = $minValue, $maxValue")
}

fun updateMinMax(value: Float) {
  if (value > maxValue)
    maxValue = value
  if (value < minValue)
    minValue = value
}
