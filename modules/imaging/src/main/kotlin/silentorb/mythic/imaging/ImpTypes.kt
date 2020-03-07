package silentorb.mythic.imaging

import silentorb.imp.core.Parameter
import silentorb.imp.core.Signature
import silentorb.imp.core.floatKey
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.spatial.Vector2

val relativeDimensionsFunction = CompleteFunction(
    path = relativeDimensionsKey,
    signature = Signature(
        parameters = listOf(
            Parameter("width", floatKey),
            Parameter("height", floatKey)
        ),
        output = relativeDimensionsKey
    ),
    implementation = { arguments ->
      Vector2(arguments["width"] as Float, arguments["height"] as Float)
    }
)
