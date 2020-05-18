package silentorb.mythic.imaging.texturing

import silentorb.imp.core.Parameter
import silentorb.imp.core.Signature
import silentorb.imp.core.floatType
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.spatial.Vector2

val relativeDimensionsFunction = CompleteFunction(
    path = relativeDimensionsKey,
    signature = Signature(
        parameters = listOf(
            Parameter("width", floatType),
            Parameter("height", floatType)
        ),
        output = relativeDimensionsType
    ),
    implementation = { arguments ->
      Vector2(arguments["width"] as Float, arguments["height"] as Float)
    }
)
