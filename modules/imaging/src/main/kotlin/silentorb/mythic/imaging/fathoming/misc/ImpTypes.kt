package silentorb.mythic.imaging.fathoming

import silentorb.imp.core.PathKey
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.spatial.Vector3
import java.nio.FloatBuffer

const val fathomPath = "silentorb.mythic.fathom"

val distanceFunctionKey = PathKey(fathomPath, "DistanceFunction")
val distanceFunctionType = distanceFunctionKey.hashCode()
//typealias Sampler3dFloat = (Float, Float, Float) -> Float
typealias Sampler3d = (Float, Float, Float, FloatBuffer) -> Unit

typealias DistanceFunction = (Vector3) -> Float

val vector3Key = PathKey(fathomPath, "Vector3")
val vector3Type = vector3Key.hashCode()
val translation3Key = PathKey(fathomPath, "Translation3")
val translation3Type = translation3Key.hashCode()
val quaternionKey = PathKey(fathomPath, "Quaternion")
val quaternionType = quaternionKey.hashCode()

fun fathomAliases() = listOf(
    TypeAlias(
        path = translation3Type,
        alias = vector3Type,
        numericConstraint = null
    )
)
