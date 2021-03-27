package silentorb.mythic.lookinglass

import org.lwjgl.opengl.GL15.glGenQueries
import org.lwjgl.opengl.GL33.*
import silentorb.mythic.debugging.MetricMap
import silentorb.mythic.debugging.Metrics
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.debugging.printProfiler

val gpuProfileMeasurements: MetricMap = mutableMapOf()

fun profileGpu(key: String, operation: () -> Unit) {
  if (getDebugBoolean("PROFILE_OPENGL")) {
    val startQuery = glGenQueries()
    glQueryCounter(startQuery, GL_TIMESTAMP)
    operation()
    val endQuery = glGenQueries()
    glQueryCounter(endQuery, GL_TIMESTAMP)
    val startTime = glGetQueryObjectui64(startQuery, GL_QUERY_RESULT)
    val endTime = glGetQueryObjectui64(endQuery, GL_QUERY_RESULT)
    val duration = endTime - startTime
    val previous = gpuProfileMeasurements[key] ?: Metrics()
    previous.iterations++
    previous.total += duration
    previous.last = duration
    gpuProfileMeasurements[key] = previous
  } else {
    operation()
  }
}

fun logGpuProfiling() {
  if (getDebugBoolean("PROFILE_OPENGL")) {
    printProfiler(gpuProfileMeasurements)
  }
}
