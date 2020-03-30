package silentorb.mythic.imaging.texturing.math

import silentorb.imp.core.Parameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.core.floatKey
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.spatial.Vector2

const val mathPath = "silentorb.mythic.math"

val vector2Key = PathKey(mathPath, "Vector2")

val vector2Function = CompleteFunction(
    path = vector2Key,
    signature = Signature(
        parameters = listOf(
            Parameter("x", floatKey),
            Parameter("y", floatKey)
        ),
        output = vector2Key
    ),
    implementation = { arguments ->
      Vector2(arguments["x"] as Float, arguments["y"] as Float)
    }
)

fun mathFunctions() =
    listOf(
        vector2Function
    )
