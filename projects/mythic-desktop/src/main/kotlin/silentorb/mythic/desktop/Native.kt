package silentorb.mythic.desktop

class Native {

  companion object {
    init {
      System.loadLibrary("mythic")
    }

    @JvmStatic
    external fun messageBox(window: Long?, title: String, message: String): Long
  }
}
