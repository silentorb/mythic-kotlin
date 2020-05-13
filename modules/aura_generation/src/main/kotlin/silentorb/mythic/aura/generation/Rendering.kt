package silentorb.mythic.aura.generation

import org.lwjgl.BufferUtils
import java.nio.ShortBuffer

fun floatSampleToShort(value: Float): Short =
    (value * Short.MAX_VALUE).toShort()

fun renderAudioTo16bit(config: AudioConfig, output: AudioOutput): ShortBuffer {
  val channelCount = output.samplers.size
  val sampleRate = config.sampleRate
  assert(channelCount == 1 || channelCount == 2)
  val start = resolveTime(output.start, sampleRate)
  val end = resolveTime(output.end, sampleRate)
  val timeLength = end - start
  if (end < start)
    throw Error("Invalid time range")

  val bufferLength = timeLength * channelCount
  val buffer = BufferUtils.createShortBuffer(bufferLength)
var i = 0
  val iStep = timeLength / 200
  for (step in (start until end)) {
    for (sampler in output.samplers) {
      val sample = sampler(sampleRate, step.toDouble())
      val shortValue = floatSampleToShort(sample)
      i++
      if (i % iStep == 0) {
        println(shortValue)
      }
      buffer.put(shortValue)
    }
  }
  buffer.rewind()
  return buffer
}
