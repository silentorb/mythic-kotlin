package silentorb.mythic.fathom.spatial

import silentorb.imp.core.PathKey
import silentorb.imp.core.newTypePair

const val spatialPath = "silentorb.mythic.spatial"

val matrix4Type = newTypePair(PathKey(spatialPath, "Matrix4"))
val vector3Type = newTypePair(PathKey(spatialPath, "Vector3"))
val translation3Type = newTypePair(PathKey(spatialPath, "Translation3"))
val quaternionType = newTypePair(PathKey(spatialPath, "Quaternion"))
