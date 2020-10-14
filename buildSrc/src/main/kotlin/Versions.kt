import org.apache.tools.ant.taskdefs.condition.Os

object Versions {
  const val gdx = "1.9.9"
  const val kotlin = "1.4.0"
  const val lwjgl = "3.2.3"
}

object Natives {
  val lwjgl = when {
    Os.isFamily(Os.FAMILY_WINDOWS) -> "natives-windows"
    Os.isFamily(Os.FAMILY_UNIX) -> "natives-linux"
    Os.isFamily(Os.FAMILY_MAC) -> "natives-macos"
    else -> throw Error("Unsupported Operating System")
  }
}
