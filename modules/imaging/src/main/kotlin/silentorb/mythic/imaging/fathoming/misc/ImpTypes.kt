package silentorb.mythic.imaging.fathoming

import silentorb.imp.core.PathKey
import silentorb.mythic.spatial.Vector3
import java.nio.FloatBuffer

const val substancePath = "silentorb.mythic.substance"

val sampler3dFloatKey = PathKey(substancePath, "Sampler3d")
typealias Sampler3dFloat = (Float, Float, Float) -> Float
typealias Sampler3d = (Float, Float, Float, FloatBuffer) -> Unit

typealias DistanceFunction = (Vector3) -> Float
