package silentorb.mythic.imaging.texturing.math

import silentorb.imp.core.*
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.spatial.Vector2

const val mathPath = "silentorb.mythic.math"

val vector2Type = newTypePair(PathKey(mathPath, "Vector2"))

val vector2Function = CompleteFunction(
    path = vector2Type.key,
    signature = CompleteSignature(
        parameters = listOf(
            CompleteParameter("x", floatType),
            CompleteParameter("y", floatType)
        ),
        output = vector2Type
    ),
    implementation = { arguments ->
      Vector2(arguments["x"] as Float, arguments["y"] as Float)
    }
)

fun mathFunctions() =
    listOf(
        vector2Function
    )
