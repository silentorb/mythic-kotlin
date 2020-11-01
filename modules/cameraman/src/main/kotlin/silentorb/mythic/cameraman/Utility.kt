package silentorb.mythic.cameraman

import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector2

fun toQuaternion(x: Float, y: Float): Quaternion =
    Quaternion()
        .rotateZ(x)
        .rotateY(-y)

fun toQuaternion(value: Vector2): Quaternion =
    Quaternion()
        .rotateZ(value.x)
        .rotateY(-value.y)
