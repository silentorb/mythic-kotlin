package silentorb.mythic.imaging.substance.functions

import silentorb.imp.core.Parameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.core.floatKey
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.imaging.substance.sampler3dFloatKey
import silentorb.mythic.imaging.substance.sphere
import silentorb.mythic.imaging.substance.substancePath
import silentorb.mythic.spatial.Vector3

fun sphereFunctions() = listOf(
    CompleteFunction(
        path = PathKey(substancePath, "sphere"),
        signature = Signature(
            parameters = listOf(
                Parameter("radius", floatKey)
            ),
            output = sampler3dFloatKey
        ),
        implementation = { arguments ->
          val radius = arguments["radius"] as Float
          sphere(radius)
        }
    )
)
