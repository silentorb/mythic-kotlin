package silentorb.mythic.imaging.texturing

import silentorb.imp.core.*
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.spatial.Vector2

val relativeDimensionsFunction = CompleteFunction(
    path = relativeDimensionsType.key,
    signature = CompleteSignature(
        parameters = listOf(
            CompleteParameter("width", floatType),
            CompleteParameter("height", floatType)
        ),
        output = relativeDimensionsType
    ),
    implementation = { arguments ->
      Vector2(arguments["width"] as Float, arguments["height"] as Float)
    }
)
