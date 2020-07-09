package silentorb.mythic.fathom.functions

import silentorb.imp.core.CompleteParameter
import silentorb.imp.core.PathKey
import silentorb.imp.core.CompleteSignature
import silentorb.imp.core.floatType
import silentorb.imp.execution.CompleteFunction
import silentorb.mythic.fathom.*
import silentorb.mythic.fathom.misc.*
import silentorb.mythic.fathom.spatial.matrix4Type
import silentorb.mythic.fathom.spatial.quaternionType
import silentorb.mythic.fathom.spatial.translation3Type
import silentorb.mythic.fathom.spatial.vector3Type
import silentorb.mythic.fathom.surfacing.getSceneDecimalBounds
import silentorb.mythic.imaging.texturing.FloatSampler3d
import silentorb.mythic.imaging.texturing.filters.*
import silentorb.mythic.imaging.texturing.rgbColorType
import silentorb.mythic.scenery.Box
import silentorb.mythic.scenery.CompositeShape
import silentorb.mythic.scenery.Shape
import silentorb.mythic.scenery.ShapeTransform
import silentorb.mythic.spatial.Matrix
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
            newShading(
                color = colorize(sampler(location))
            )
          }
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "newModel"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("form", distanceFunctionType),
                CompleteParameter("shading", rgbSampler3dType),
                CompleteParameter("collision", shapeType)
            ),
            output = modelFunctionType
        ),
        implementation = { arguments ->
          val form = arguments["form"]!! as DistanceFunction
          val color = arguments["shading"]!! as ShadingFunction
          val collision = arguments["collision"]!! as Shape
          ModelFunction(
              form = form,
              shading = color,
              collision = collision
          )
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "newModel"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("form", distanceFunctionType),
                CompleteParameter("shading", rgbSampler3dType)
            ),
            output = modelFunctionType
        ),
        implementation = { arguments ->
          val form = arguments["form"]!! as DistanceFunction
          val color = arguments["shading"]!! as ShadingFunction
          ModelFunction(
              form = form,
              shading = color,
              collision = null
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
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "collisionBox"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("form", distanceFunctionType)
            ),
            output = shapeType
        ),
        implementation = { arguments ->
          val form = arguments["form"] as DistanceFunction
          val bounds = getSceneDecimalBounds(form)
          Box(
              halfExtents = bounds.dimensions / 2f
          )
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "collisionBox"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("dimensions", vector3Type)
            ),
            output = shapeType
        ),
        implementation = { arguments ->
          val dimensions = arguments["dimensions"] as Vector3
          Box(
              halfExtents = dimensions / 2f
          )
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "transform"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("transform", matrix4Type),
                CompleteParameter("shape", shapeType)
            ),
            output = shapeType
        ),
        implementation = { arguments ->
          val transform = arguments["transform"] as Matrix
          val shape = arguments["shape"] as Shape
          ShapeTransform(
              transform = transform,
              shape = shape
          )
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "+"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("first", shapeType),
                CompleteParameter("second", shapeType)
            ),
            output = shapeType
        ),
        implementation = { arguments ->
            val first = arguments["first"] as Shape
            val second = arguments["second"] as Shape

            fun flatten(shape: Shape) =
                if (shape is CompositeShape)
                    shape.shapes
                else
                    listOf(shape)
            
            CompositeShape(
                shapes = flatten(first) + flatten(second)
            )
        }
    )
)
