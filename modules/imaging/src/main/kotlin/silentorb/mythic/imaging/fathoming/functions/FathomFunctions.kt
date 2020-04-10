package silentorb.mythic.imaging.fathoming.functions

import silentorb.imp.core.Parameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.core.floatKey
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.imaging.fathoming.*
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.degreesToRadians

fun fathomFunctions() = listOf(

    CompleteFunction(
        path = PathKey(fathomPath, "sphere"),
        signature = Signature(
            parameters = listOf(
                Parameter("radius", floatKey)
            ),
            output = distanceFunctionKey
        ),
        implementation = { arguments ->
            val radius = arguments["radius"] as Float
            sphere(radius)
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "cube"),
        signature = Signature(
            parameters = listOf(
                Parameter("dimensions", vector3Key)
            ),
            output = distanceFunctionKey
        ),
        implementation = { arguments ->
            val dimensions = arguments["dimensions"] as Vector3
            cube(dimensions)
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "translate"),
        signature = Signature(
            parameters = listOf(
                Parameter("offset", translation3Key),
                Parameter("source", distanceFunctionKey)
            ),
            output = distanceFunctionKey
        ),
        implementation = { arguments ->
            val offset = arguments["offset"] as Vector3
            val source = arguments["source"] as DistanceFunction
            translate(offset, source)
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "rotate"),
        signature = Signature(
            parameters = listOf(
                Parameter("rotation", quaternionKey),
                Parameter("source", distanceFunctionKey)
            ),
            output = distanceFunctionKey
        ),
        implementation = { arguments ->
          val orientation = arguments["rotation"] as Quaternion
          val source = arguments["source"] as DistanceFunction
          rotate(orientation, source)
        }
    ),

    CompleteFunction(
        path = vector3Key,
        signature = Signature(
            parameters = listOf(
                Parameter("x", floatKey),
                Parameter("y", floatKey),
                Parameter("z", floatKey)
            ),
            output = vector3Key
        ),
        implementation = { arguments ->
            Vector3(arguments["x"] as Float, arguments["y"] as Float, arguments["z"] as Float)
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "rotationFromAxis"),
        signature = Signature(
            parameters = listOf(
                Parameter("x", floatKey),
                Parameter("y", floatKey),
                Parameter("z", floatKey)
            ),
            output = quaternionKey
        ),
        implementation = { arguments ->
          Quaternion()
              .rotateZ(degreesToRadians(arguments["z"] as Float))
              .rotateY(degreesToRadians(arguments["y"] as Float))
              .rotateX(degreesToRadians(arguments["x"] as Float))
        }
    )

)
