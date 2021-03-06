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
import silentorb.mythic.imaging.texturing.*
import silentorb.mythic.imaging.texturing.filters.*
import silentorb.mythic.scenery.*
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Quaternion
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.degreesToRadians
import kotlin.math.max

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
        path = PathKey(fathomPath, "capsule"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("radius", floatType),
                CompleteParameter("height", floatType)
            ),
            output = distanceFunctionType
        ),
        implementation = { arguments ->
          val radius = arguments["radius"] as Float
          val height = arguments["height"] as Float
          capsule(radius, height)
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "cylinder"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("radius", floatType),
                CompleteParameter("height", floatType)
            ),
            output = distanceFunctionType
        ),
        implementation = { arguments ->
          val radius = arguments["radius"] as Float
          val height = arguments["height"] as Float
          cylinder(radius, height)
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
        path = PathKey(fathomPath, "scale"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("offset", vector3Type),
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
          noise3d(arguments, nonTilingOpenSimplex3D(variation.toLong()), anonymousSampler)
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
            output = shadingSamplerType
        ),
        implementation = { arguments ->
          val sampler = arguments["sampler"]!! as DistanceSampler
          val colorize = colorizeValue(arguments)
          val result: ShadingFunction = { origin ->
            newShading(
                color = colorize(sampler(origin).second)
            )
          }
          result
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "newShading"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("color", rgbColorType),
                CompleteParameter("opacity", floatType),
                CompleteParameter("glow", floatType)
            ),
            output = shadingSamplerType
        ),
        implementation = { arguments ->
          val color = rgbIntToFloat(arguments["color"]!! as RgbColor)
          val opacity = arguments["opacity"]!! as Float
          val glow = arguments["glow"]!! as Float
          val result: ShadingFunction = { origin ->
            Shading(
                color = color,
                opacity = opacity,
                glow = glow,
                specular = 0.8f
            )
          }
          result
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "newModel"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("form", distanceFunctionType),
                CompleteParameter("shading", shadingSamplerType),
                CompleteParameter("collision", shapeType)
            ),
            output = modelFunctionType
        ),
        implementation = { arguments ->
          val form = arguments["form"]!! as DistanceFunction
          val shading = arguments["shading"]!! as ShadingFunction
          val collision = arguments["collision"]!! as Shape
          ModelFunction(
              form = form,
              shading = shading,
              collision = collision
          )
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "newModel"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("form", distanceFunctionType),
                CompleteParameter("shading", shadingSamplerType)
            ),
            output = modelFunctionType
        ),
        implementation = { arguments ->
          val form = arguments["form"]!! as DistanceFunction
          val shading = arguments["shading"]!! as ShadingFunction
          ModelFunction(
              form = form,
              shading = shading,
              collision = null
          )
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "+"),
        signature = CompleteSignature(
            isVariadic = true,
            parameters = listOf(
                CompleteParameter("values", modelFunctionType)
            ),
            output = modelFunctionType
        ),
        implementation = { arguments ->
          val models = arguments["values"]!! as List<ModelFunction>
          mergeModelFunctions(models)
        }
    ),

    CompleteFunction(
        path = PathKey(fathomPath, "deform"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("object", distanceFunctionType),
                CompleteParameter("deformer", floatSampler3dType),
                CompleteParameter("amplitude", floatType)
                ),
            output = distanceFunctionType
        ),
        implementation = { arguments ->
          val first = arguments["object"] as DistanceFunction
          val deformer = arguments["deformer"] as DistanceFunction
          val amplitude = arguments["amplitude"] as Float
          deformer3dSampler(first, deformer, amplitude)
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
        path = PathKey(fathomPath, "triangularPrism"),
        signature = CompleteSignature(
            parameters = listOf(
                CompleteParameter("dimensions", vector3Type)
            ),
            output = shapeType
        ),
        implementation = { arguments ->
          val dimensions = arguments["dimensions"] as Vector3
          val half = dimensions / 2f
          val up = listOf(
              Vector3(-half.x, -half.y, half.z),
              Vector3(half.x, -half.y, half.z),
              Vector3(half.x, half.y, half.z)
          )
          val down = up.map { it.copy(z = -half.z) }
          MeshShape(
              triangles = listOf(
                  up[0], up[1], up[2],
                  down[0], down[1], down[2]
              ),
              radius = max(dimensions.x, dimensions.y) / 2f,
              height = dimensions.z
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
            isVariadic = true,
            parameters = listOf(
                CompleteParameter("values", shapeType)
            ),
            output = shapeType
        ),
        implementation = { arguments ->
          val values = arguments["values"] as List<Shape>
          CompositeShape(
              shapes = values
          )
        }
    )
)
