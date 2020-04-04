package silentorb.mythic.imaging.fathoming

import silentorb.imp.core.PathKey
import silentorb.imp.core.intKey
import silentorb.imp.core.newNumericConstraint
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.imaging.texturing.filters.oneToOneHundredKey
import silentorb.mythic.spatial.Vector3
import java.nio.FloatBuffer

const val fathomPath = "silentorb.mythic.fathom"

val distanceFunctionKey = PathKey(fathomPath, "DistanceFunction")
//typealias Sampler3dFloat = (Float, Float, Float) -> Float
typealias Sampler3d = (Float, Float, Float, FloatBuffer) -> Unit

typealias DistanceFunction = (Vector3) -> Float

val vector3Key = PathKey(fathomPath, "Vector3")
val translation3Key = PathKey(fathomPath, "Translation3")

fun fathomAliases() = listOf(
    TypeAlias(
        path = translation3Key,
        alias = vector3Key,
        numericConstraint = null
    )
)
