package silentorb.mythic.imaging.fathoming

import silentorb.imp.core.PathKey
import silentorb.imp.core.newTypePair
import silentorb.imp.execution.TypeAlias
import silentorb.imp.execution.typePairstoTypeNames
import silentorb.mythic.imaging.texturing.FloatSampler3d
import silentorb.mythic.imaging.texturing.texturingPath
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.scenery.Shape
import java.nio.FloatBuffer

const val fathomPath = "silentorb.mythic.fathom"

val distanceFunctionType = newTypePair(PathKey(fathomPath, "DistanceFunction"))
val modelFunctionType = newTypePair(PathKey(fathomPath, "ModelFunction"))
val vector3Type = newTypePair(PathKey(fathomPath, "Vector3"))
val translation3Type = newTypePair(PathKey(fathomPath, "Translation3"))
val quaternionType = newTypePair(PathKey(fathomPath, "Quaternion"))
val floatSampler3dType = newTypePair(PathKey(texturingPath, "FloatSampler3d"))
val rgbSampler3dType = newTypePair(PathKey(texturingPath, "RgbSampler3d"))

typealias Sampler3d = (Float, Float, Float, FloatBuffer) -> Unit

typealias DistanceFunction = FloatSampler3d
typealias RgbColorFunction = (Vector3) -> Vector3

data class ModelFunction(
    val distance: DistanceFunction,
    val color: RgbColorFunction,
    val collision: Shape?
)

fun fathomAliases() = listOf(
    TypeAlias(
        path = translation3Type.hash,
        alias = vector3Type.hash,
        numericConstraint = null
    )
)

fun fathomTypes() =
    typePairstoTypeNames(
        listOf(
            distanceFunctionType,
            modelFunctionType,
            vector3Type,
            translation3Type,
            quaternionType,
            floatSampler3dType,
            rgbSampler3dType
        )
    )
