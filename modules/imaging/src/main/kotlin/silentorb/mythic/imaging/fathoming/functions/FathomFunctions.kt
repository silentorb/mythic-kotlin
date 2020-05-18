package silentorb.mythic.imaging.fathoming.functions

import silentorb.imp.core.*
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
                Parameter("radius", floatType)
            ),
            output = distanceFunctionType
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
                Parameter("dimensions", vector3Type)
            ),
            output = distanceFunctionType
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
                Parameter("offset", translation3Type),
                Parameter("source", distanceFunctionType)
            ),
            output = distanceFunctionType
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
                Parameter("rotation", quaternionType),
                Parameter("source", distanceFunctionType)
            ),
            output = distanceFunctionType
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
                Parameter("x", floatType),
                Parameter("y", floatType),
                Parameter("z", floatType)
            ),
            output = vector3Type
        ),
        implementation = { arguments ->
            Vector3(arguments["x"] as Float, arguments["y"] as Float, arguments["z"] as Float)
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "rotationFromAxis"),
        signature = Signature(
            parameters = listOf(
                Parameter("x", floatType),
                Parameter("y", floatType),
                Parameter("z", floatType)
            ),
            output = quaternionType
        ),
        implementation = { arguments ->
          Quaternion()
              .rotateZ(degreesToRadians(arguments["z"] as Float))
              .rotateY(degreesToRadians(arguments["y"] as Float))
              .rotateX(degreesToRadians(arguments["x"] as Float))
        }
    )

)
