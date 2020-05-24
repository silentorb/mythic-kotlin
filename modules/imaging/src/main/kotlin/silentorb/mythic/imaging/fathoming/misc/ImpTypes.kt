package silentorb.mythic.imaging.fathoming

import silentorb.imp.core.PathKey
import silentorb.imp.execution.TypeAlias
import silentorb.mythic.imaging.texturing.FloatSampler3d
import silentorb.mythic.imaging.texturing.texturingPath
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import java.nio.FloatBuffer

const val fathomPath = "silentorb.mythic.fathom"

val modelFunctionKey = PathKey(fathomPath, "ModelFunction")
val modelFunctionType = modelFunctionKey.hashCode()

typealias Sampler3d = (Float, Float, Float, FloatBuffer) -> Unit

typealias DistanceFunction = FloatSampler3d
typealias RgbColorFunction = (Vector3) -> Vector3

data class ModelFunction(
    val distance: DistanceFunction,
    val color: RgbColorFunction
)

val vector3Key = PathKey(fathomPath, "Vector3")
val vector3Type = vector3Key.hashCode()
val translation3Key = PathKey(fathomPath, "Translation3")
val translation3Type = translation3Key.hashCode()
val quaternionKey = PathKey(fathomPath, "Quaternion")
val quaternionType = quaternionKey.hashCode()

val floatSampler3dKey = PathKey(texturingPath, "FloatSampler")
val floatSampler3dType = floatSampler3dKey.hashCode()

val rgbSampler3dKey = PathKey(texturingPath, "RgbSampler")
val rgbSampler3dType = rgbSampler3dKey.hashCode()

fun fathomAliases() = listOf(
    TypeAlias(
        path = translation3Type,
        alias = vector3Type,
        numericConstraint = null
    )
)
