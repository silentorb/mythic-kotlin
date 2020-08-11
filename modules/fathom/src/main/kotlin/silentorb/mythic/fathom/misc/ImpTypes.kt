package silentorb.mythic.fathom.misc

import silentorb.imp.core.PathKey
import silentorb.imp.core.newTypePair
import silentorb.imp.execution.TypeAlias
import silentorb.imp.execution.typePairsToTypeNames
import silentorb.mythic.fathom.spatial.quaternionType
import silentorb.mythic.fathom.spatial.translation3Type
import silentorb.mythic.fathom.spatial.vector3Type
import silentorb.mythic.imaging.texturing.DistanceSampler
import silentorb.mythic.imaging.texturing.texturingPath
import silentorb.mythic.scenery.Shading
import silentorb.mythic.spatial.Vector3
import java.nio.FloatBuffer

const val fathomPath = "silentorb.mythic.fathom"

val distanceFunctionType = newTypePair(PathKey(fathomPath, "DistanceFunction"))
val modelFunctionType = newTypePair(PathKey(fathomPath, "ModelFunction"))
val floatSampler3dType = newTypePair(PathKey(texturingPath, "FloatSampler3d"))
val shadingSamplerType = newTypePair(PathKey(fathomPath, "ShadingSampler"))
val shapeType = newTypePair(PathKey(fathomPath, "Shape"))

typealias Sampler3d = (Float, Float, Float, FloatBuffer) -> Unit

typealias DistanceFunction = DistanceSampler
typealias RgbColorFunction = (Vector3) -> Vector3
typealias ShadingFunction = (Vector3) -> Shading

fun fathomAliases() = listOf(
    TypeAlias(
        path = translation3Type.hash,
        alias = vector3Type.hash,
        numericConstraint = null
    )
)

fun fathomTypes() =
    typePairsToTypeNames(
        listOf(
            distanceFunctionType,
            modelFunctionType,
            vector3Type,
            translation3Type,
            quaternionType,
            floatSampler3dType,
            shadingSamplerType
        )
    )
