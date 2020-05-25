package silentorb.mythic.imaging.fathoming.functions

import silentorb.imp.core.CompleteParameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.CompleteSignature
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
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("radius", floatType)
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
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("dimensions", vector3Type)
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
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("offset", translation3Type),
                CompleteParameter("source", distanceFunctionType)
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
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("rotation", quaternionType),
                CompleteParameter("source", distanceFunctionType)
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
        path = vector3Type.key,
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("x", floatType),
                CompleteParameter("y", floatType),
                CompleteParameter("z", floatType)
            ),
            output = vector3Type
        ),
        implementation = { arguments ->
            Vector3(arguments["x"] as Float, arguments["y"] as Float, arguments["z"] as Float)
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "rotationFromAxis"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("x", floatType),
                CompleteParameter("y", floatType),
                CompleteParameter("z", floatType)
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
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("scale", oneToOneHundredType),
                CompleteParameter("detail", zeroToOneHundredType),
                CompleteParameter("variation", noiseVariationType)
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
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("sampler", floatSampler3dType),
                CompleteParameter("firstColor", rgbColorType),
                CompleteParameter("secondColor", rgbColorType)
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
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("distance", distanceFunctionType),
                CompleteParameter("color", rgbSampler3dType)
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
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "deform"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("object", distanceFunctionType),
                CompleteParameter("deformer", floatSampler3dType)
            ),
            output = distanceFunctionType
        ),
        implementation = { arguments ->
            val first = arguments["object"] as DistanceFunction
            val deformer = arguments["deformer"] as DistanceFunction
            deformer3dSampler(first, deformer)
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "*"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("sampler", floatSampler3dType),
                CompleteParameter("constant", floatType)
            ),
            output = floatSampler3dType
        ),
        implementation = { arguments ->
            val sampler = arguments["sampler"] as DistanceFunction
            val constant = arguments["constant"] as Float
            times3dSampler(sampler, constant)
        }
    )
)
