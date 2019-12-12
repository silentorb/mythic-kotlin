package mythic.aura

import mythic.platforming.PlatformAudio
import java.nio.ByteBuffer

private var mixBuffer: ByteBuffer? = null

fun getMixBuffer(bufferSize: Int): ByteBuffer {
  val buffer = mixBuffer
  if (buffer == null || buffer.capacity() < bufferSize) {
    mixBuffer = ByteBuffer.allocate(bufferSize)
  }
  else {
    buffer.rewind()
  }

  return mixBuffer!!
}

fun updateAudioDeviceBuffer(audio: PlatformAudio, buffer: ByteBuffer, bufferSize: Int) {
  val b = ByteArray(bufferSize)
  buffer.position(0)
  buffer.get(b, 0, bufferSize)
  audio.update(b)
}