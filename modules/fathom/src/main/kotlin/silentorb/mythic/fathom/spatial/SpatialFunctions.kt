package silentorb.mythic.fathom.spatial

import silentorb.imp.core.CompleteParameter
import silentorb.imp.core.CompleteSignature
import silentorb.imp.core.PathKey
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3

fun spatialFunctions(): List<CompleteFunction> = listOf(
    CompleteFunction(
        path = PathKey(spatialPath, "newMatrix"),
        signature = CompleteSignature(
            parameters = listOf(),
            output = matrix4Type
        ),
        implementation = { _ ->
          Matrix.identity
        }
    ),

    CompleteFunction(
        path = PathKey(spatialPath, "translate"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("offset", translation3Type),
                CompleteParameter("matrix", matrix4Type)
            ),
            output = matrix4Type
        ),
        implementation = { arguments ->
          val offset = arguments["offset"] as Vector3
          val matrix = arguments["matrix"] as Matrix
          matrix.translate(offset)
        }
    ),

    CompleteFunction(
        path = PathKey(spatialPath, "rotate"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("rotation", quaternionType),
                CompleteParameter("matrix", matrix4Type)
            ),
            output = matrix4Type
        ),
        implementation = { arguments ->
          val rotation = arguments["rotation"] as Quaternion
          val matrix = arguments["matrix"] as Matrix
          matrix.rotate(rotation)
        }
    ),

    CompleteFunction(
        path = PathKey(spatialPath, "scale"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("scale", translation3Type),
                CompleteParameter("matrix", matrix4Type)
            ),
            output = matrix4Type
        ),
        implementation = { arguments ->
          val scale = arguments["scale"] as Vector3
          val matrix = arguments["matrix"] as Matrix
          matrix.scale(scale)
        }
    )
)
