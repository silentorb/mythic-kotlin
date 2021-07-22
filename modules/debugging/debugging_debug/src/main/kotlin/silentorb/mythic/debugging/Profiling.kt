package silentorb.mythic.debugging

import silentorb.mythic.logging.logger

data class Metrics(
    var iterations: Int = 0,
    var total: Long = 0L,
    var last: Long = 0L,
)

typealias MetricMap = MutableMap<String, Metrics>

class Profiler {
  val metrics: MetricMap = mutableMapOf()

  fun <T> wrapBlock(name: String, action: () -> T): T {
    val record = metrics.getOrElse(name) { Metrics(0, 0L) }
    val start = System.nanoTime()
    val result = action()
    record.total += System.nanoTime() - start
    record.iterations++
    metrics[name] = record
    return result
  }

  fun <I, O> wrap(name: String, action: (I) -> O): (I) -> O = { input ->
    val record = metrics.getOrElse(name) { Metrics(0, 0L) }
    val start = System.nanoTime()
    val result = action(input)
    record.total += System.nanoTime() - start
    record.iterations++
    metrics[name] = record
    result
  }
}

fun printProfiler(metrics: MetricMap) {
  for ((name, metric) in metrics) {
    println(
        name.take(12).padStart(12, ' ')
            + " total: "
            + String.format("%,d", metric.total).padStart(18, ' ')
            + "   average: "
            + String.format("%,d", metric.total / metric.iterations.toLong()).padStart(18, ' ')
    )
  }
}

private var profiler: Profiler? = null

fun globalProfiler(): Profiler {
  if (profiler == null)
    profiler = Profiler()

  return profiler!!
}

fun <T> logExecutionTime(label: String, condition: Boolean, block: () -> T): T {
  return if (condition) {
    val start = System.currentTimeMillis()
    val result = block()
    val end = System.currentTimeMillis()
    val duration = end - start
    logger.debug("Measurement in ms: $label: $duration")
    result
  } else
    block()
}
