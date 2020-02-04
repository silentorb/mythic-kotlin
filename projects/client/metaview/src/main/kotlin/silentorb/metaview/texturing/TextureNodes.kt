//package silentorb.metaview.texturing
//
//import silentorb.metaview.common.*
//import silentorb.mythic.spatial.Vector3
//
//data class FloatRange(
//    val min: Float,
//    val max: Float
//)
//
//const val textureOutput = "textureOutput"
//
//val nodeDefinitions: NodeDefinitionMap = mapOf(
//    "coloredCheckers" to NodeDefinition(
//        inputs = mapOf(
//            "firstColor" to InputDefinition(
//                type = colorType,
//                defaultValue = Vector3(0f)
//            ),
//            "secondColor" to InputDefinition(
//                type = colorType,
//                defaultValue = Vector3(1f)
//            )
//        ),
//        outputType = bitmapType
//    ),
//    "solidColor" to NodeDefinition(
//        inputs = mapOf(
//            "color" to InputDefinition(
//                type = colorType,
//                defaultValue = Vector3(0f)
//            )
//        ),
//        outputType = bitmapType
//    ),
//    "checkers" to NodeDefinition(
//        inputs = mapOf(),
//        outputType = grayscaleType
//    ),
//    "colorize" to NodeDefinition(
//        inputs = mapOf(
//            "grayscale" to InputDefinition(
//                type = grayscaleType
//            ),
//            "firstColor" to InputDefinition(
//                type = colorType,
//                defaultValue = Vector3(0f)
//            ),
//            "secondColor" to InputDefinition(
//                type = colorType,
//                defaultValue = Vector3(1f)
//            )
//        ),
//        outputType = bitmapType
//    ),
//    "toneMap" to NodeDefinition(
//        inputs = mapOf(
//            "input" to InputDefinition(
//                type = grayscaleType
//            )
//        ),
//        outputType = grayscaleType
//    ),
//    "mask" to NodeDefinition(
//        inputs = mapOf(
//            "first" to InputDefinition(
//                type = bitmapType
//            ),
//            "second" to InputDefinition(
//                type = bitmapType
//            ),
//            "mask" to InputDefinition(
//                type = grayscaleType
//            )
//        ),
//        outputType = bitmapType
//    ),
//    "mixBitmaps" to NodeDefinition(
//        inputs = mapOf(
//            "mixer" to InputDefinition(
//                type = weightsType,
//                defaultValue = listOf<Float>()
//            )
//        ),
//        variableInputs = bitmapType,
//        outputType = bitmapType
//    ),
//    "mixGrayscales" to NodeDefinition(
//        inputs = mapOf(
//            "weights" to InputDefinition(
//                type = weightsType,
//                defaultValue = listOf<Float>()
//            )
//        ),
//        variableInputs = grayscaleType,
//        outputType = grayscaleType
//    ),
//    "perlinNoise" to NodeDefinition(
//        inputs = mapOf(
//            "offset" to InputDefinition(
//                type = intType,
//                defaultValue = 0
//            ),
//            "periods" to InputDefinition(
//                type = intType,
//                defaultValue = 8
//            )
//        ),
//        outputType = grayscaleType
//    ),
//    "illumination" to NodeDefinition(
//        inputs = mapOf(
//            "depth" to InputDefinition(
//                type = depthsType
//            ),
//            "position" to InputDefinition(
//                type = positionsType
//            ),
//            "normal" to InputDefinition(
//                type = normalsType
//            )
//        ),
//        outputType = grayscaleType
//    ),
//    "mixScene" to NodeDefinition(
//        inputs = mapOf(
//            "color" to InputDefinition(
//                type = bitmapType
//            ),
//            "illumination" to InputDefinition(
//                type = grayscaleType
//            )
//        ),
//        outputType = bitmapType
//    ),
//    "rayMarch" to NodeDefinition(
//        inputs = mapOf(
//        ),
//        outputType = multiType,
//        outputs = mapOf(
//            "color" to bitmapType,
//            "depth" to depthsType,
//            "position" to positionsType,
//            "normal" to normalsType
//        )
//    ),
//    "voronoiBoundaries" to NodeDefinition(
//        inputs = mapOf(
//        ),
//        outputType = grayscaleType
//    ),
//    textureOutput to NodeDefinition(
//        inputs = mapOf(
//            "diffuse" to InputDefinition(
//                type = bitmapType
//            )
//        ),
//        outputType = noneType
//    )
//)
