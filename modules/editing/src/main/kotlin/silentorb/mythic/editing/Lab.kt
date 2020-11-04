package silentorb.mythic.editing

import silentorb.mythic.spatial.*

object Lab {
  @JvmStatic
  fun main(args: Array<String>) {
    System.setProperty("joml.format", "false")
    val value = Vector3.zero
    val start = Vector3(1f, 0f, 0f)
    val end = Vector3(1f, 0f, 1f)
    val center = Vector3.zero
    val a = Quaternion().lookAlong((start - center).normalize(), Vector3.up)
    val b = Quaternion().lookAlong((end - center).normalize(), Vector3.up)
    val diff=  a.difference(b)
    val orientation = Quaternion()
        .rotateZ(value.z)
        .rotateY(value.y)
        .rotateX(value.x)
    val newOrientation = orientation * diff
    val newValue = newOrientation.getAngles()
    println(newValue)
  }
}
