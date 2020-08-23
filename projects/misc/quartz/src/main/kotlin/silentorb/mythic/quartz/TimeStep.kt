package silentorb.mythic.quartz

private const val ceiling = 1.0 / 5

data class TimeState(
    val start: Long, // Just used for reference
    val previous: Long,
    val latest: Long
)

data class TimestepState(
    val time: TimeState,
    val increment: Long,
    val rawDelta: Double,
    val accumulator: Double,
    val delta: Double
)

fun newTimeState(): TimeState {
  val start = System.nanoTime()
  return TimeState(
      start = start,
      previous = start,
      latest = start
  )
}

fun newTimestepState(): TimestepState =
    TimestepState(
        time = newTimeState(),
        rawDelta = 0.0,
        accumulator = 0.0,
        increment = 0L,
        delta = 0.0
    )

fun updateTimeState(state: TimeState): TimeState =
    state.copy(
        previous = state.latest,
        latest = System.nanoTime()
    )

fun nanosecondsToDelta(value: Long): Double =
    value.toDouble() / 1_000_000_000

fun clipDelta(max: Double): (Double) -> Double = { value -> Math.min(value, max) }

fun updateTimestep(timestepState: TimestepState, step: Double): Pair<TimestepState, Int> {
  val timeState = updateTimeState(timestepState.time)
  val increment = timeState.latest - timeState.previous
  val rawDelta = nanosecondsToDelta(increment)
  val delta = clipDelta(ceiling)(rawDelta)

  val accumulator = timestepState.accumulator + delta

  val iterationCount = if (accumulator >= step)
    (accumulator / step).toInt()
  else
    0

  val finalAccumulator = accumulator - step * iterationCount

  return Pair(
      TimestepState(
          time = timeState,
          rawDelta = rawDelta,
          increment = increment,
          accumulator = finalAccumulator,
          delta = delta
      ),
      iterationCount
  )
}
