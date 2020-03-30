package silentorb.mythic.imaging.texturing.filters

import java.nio.ByteBuffer

typealias NoiseContextPointer = Long

class NoiseContext(val pointer: NoiseContextPointer) {
  protected fun finalize() {
    NoiseNative.deleteNoiseContext(pointer)
  }
}

class NoiseNative {

  companion object {
    init {
      val url = NoiseNative::class.java.getResource("/imaging_native.dll")
      if (url == null)
        throw Error("Could not find imaging_native.dll")

      val formattedUrl = url.toString()
        .replace("jar:file:/", "")
        .replace("file:/", "")
        .replace("imaging_native.jar!/", "")
      System.load(formattedUrl)
    }

    @JvmStatic
    external fun test(): Int

    @JvmStatic
    external fun newNoiseContext(Seed: Long): NoiseContextPointer

    @JvmStatic
    external fun deleteNoiseContext(contextPointer: NoiseContextPointer)

    @JvmStatic
    external fun noise2d(contextPointer: NoiseContextPointer, x: Double, y: Double): Double

    @JvmStatic
    external fun fillNoiseBuffer2d(contextPointer: NoiseContextPointer,
                                   buffer: Long, dimensionsX: Int, dimensionsY: Int, octaves: Int)

    @JvmStatic
    external fun getAddress(buffer: ByteBuffer): Long

  }
}
