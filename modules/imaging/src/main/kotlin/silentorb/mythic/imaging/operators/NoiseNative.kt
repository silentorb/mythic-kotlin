package silentorb.mythic.imaging.operators

class NoiseNative {

  companion object {
    init {
      val url = NoiseNative::class.java.getResource("/imaging_native.dll")
      System.load(url.toString().replace("file:/", ""))
    }

    @JvmStatic
    external fun test(): Int
  }
}
