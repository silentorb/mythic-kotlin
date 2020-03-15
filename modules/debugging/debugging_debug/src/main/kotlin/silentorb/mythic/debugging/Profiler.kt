package silentorb.mythic.debugging

data class Metrics(
    var iterations: Int,
    var total: Long
)

class Profiler {
  val metrics: MutableMap<String, Metrics> = mutableMapOf()

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

fun printProfiler(profiler: Profiler) {
  for ((name, metric) in profiler.metrics) {
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
