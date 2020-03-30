package silentorb.mythic.imaging.substance

import silentorb.imp.core.PathKey
import java.nio.FloatBuffer

const val substancePath = "silentorb.mythic.substance"

val sampler3dFloatKey = PathKey(substancePath, "Sampler3d")
typealias Sampler3dFloat = (Float, Float, Float) -> Float
typealias Sampler3d = (Float, Float, Float, FloatBuffer) -> Unit
