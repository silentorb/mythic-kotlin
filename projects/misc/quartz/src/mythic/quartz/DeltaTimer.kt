package mythic.quartz

private const val ceiling = 1.0 / 5

class DeltaTimer {
  val start = System.nanoTime() // Just used for reference
  private var _last = start
  var actualDelta: Double = 0.0

  val last: Long
    get() = _last

  fun update(): Double {
    val now = System.nanoTime()
    val gap = now - _last
    val result = gap.toDouble() / 1000000000 // 1,000,000,000
    _last = now

    actualDelta = result

    return if (result > ceiling)
      ceiling
    else
      result
  }
}
