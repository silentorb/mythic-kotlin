package silentorb.mythic.imaging.fathoming.functions

import silentorb.imp.core.Parameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.Signature
import silentorb.imp.core.floatType
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.imaging.fathoming.*
import silentorb.mythic.imaging.texturing.FloatSampler3d
import silentorb.mythic.imaging.texturing.filters.*
import silentorb.mythic.imaging.texturing.rgbColorType
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
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "noise"),
        signature = Signature(
            parameters = listOf(
                Parameter("scale", oneToOneHundredType),
                Parameter("detail", zeroToOneHundredType),
                Parameter("variation", noiseVariationType)
            ),
            output = floatSampler3dType
        ),
        implementation = { arguments ->
          val variation = arguments["variation"] as Int
          noise3d(arguments, nonTilingOpenSimplex3D(variation.toLong()))
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "colorize"),
        signature = Signature(
            parameters = listOf(
                Parameter("sampler", floatSampler3dType),
                Parameter("firstColor", rgbColorType),
                Parameter("secondColor", rgbColorType)
            ),
            output = rgbSampler3dType
        ),
        implementation = { arguments ->
          val sampler = arguments["sampler"]!! as FloatSampler3d
          val colorize = colorizeValue(arguments)
          ;
          { location: Vector3 ->
            colorize(sampler(location))
          }
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "newModel"),
        signature = Signature(
            parameters = listOf(
                Parameter("distance", distanceFunctionType),
                Parameter("color", rgbSampler3dType)
            ),
            output = modelFunctionType
        ),
        implementation = { arguments ->
          val distance = arguments["distance"]!! as DistanceFunction
          val color = arguments["color"]!! as RgbColorFunction
          ModelFunction(
              distance = distance,
              color = color
          )
        }
    )
)
