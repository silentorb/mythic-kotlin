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
    val angle = a.difference(b).angle
//    val offsetOrientation = Quaternion().rotateX(angle) * Quaternion().rotateZ(Pi / 2f)
    val lookat = Vector3(1f, 1f, 0f).normalize()
    val offsetOrientation = Quaternion().rotateAxis(angle, lookat)
    println(offsetOrientation.getAngles())
    val newValue = value + offsetOrientation.getAngles()
//    println(newValue)

    val temp = offsetOrientation.getAngles()
    val orientation = Quaternion()//.rotateZYX(value.z, value.y, value.x)
            .rotateX(temp.x)
            .rotateY(temp.y)
            .rotateZ(temp.z)

    val j = orientation.getAngles()
    println("")
  }
}
