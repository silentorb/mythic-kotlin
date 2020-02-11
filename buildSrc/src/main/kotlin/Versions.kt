import org.gradle.internal.os.OperatingSystem

object Versions {
  const val gdx = "1.9.9"
  const val joml = "1.9.8"
  const val kotlin = "1.3.61"
  const val lwjgl = "3.1.5"
}

object Natives {
  val lwjgl = when (OperatingSystem.current()) {
    OperatingSystem.WINDOWS -> "natives-windows"
    OperatingSystem.LINUX -> "natives-linux"
    OperatingSystem.MAC_OS -> "natives-macos"
    else -> throw Error("Unsupported Operating System")
  }
}
