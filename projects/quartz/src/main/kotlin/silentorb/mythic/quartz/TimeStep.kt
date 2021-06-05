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
    val delta: Double,
    val fps: Int = 0,
    val fpsStepAccumulator: Int = 0,
    val fpsDurationAccumulator: Long = 0L,
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

const val nanoSecondsInSecond = 1_000_000_000

fun nanosecondsToDelta(value: Long): Double =
    value.toDouble() / nanoSecondsInSecond

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

  val fpsIncrement = if (iterationCount > 0) 1 else 0
  val fpsDurationAccumulator1 = timestepState.fpsDurationAccumulator + increment
  val nextSecond = fpsDurationAccumulator1 > nanoSecondsInSecond
  val fpsDurationAccumulator2 = fpsDurationAccumulator1 % nanoSecondsInSecond
  val fpsStepAccumulator1 = timestepState.fpsStepAccumulator + fpsIncrement
  val fpsStepAccumulator2 = if (nextSecond) 0 else fpsStepAccumulator1
  val fps = if (nextSecond) fpsStepAccumulator1 else timestepState.fps

  return Pair(
      TimestepState(
          time = timeState,
          rawDelta = rawDelta,
          increment = increment,
          accumulator = finalAccumulator,
          delta = delta,
          fpsDurationAccumulator = fpsDurationAccumulator2,
          fpsStepAccumulator = fpsStepAccumulator2,
          fps = fps,
      ),
      iterationCount
  )
}
