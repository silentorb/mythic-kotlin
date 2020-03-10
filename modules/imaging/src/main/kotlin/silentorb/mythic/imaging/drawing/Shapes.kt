package silentorb.mythic.imaging.drawing

import silentorb.mythic.spatial.Matrix3
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

typealias Id = Int

typealias ShapePoints = List<Vector2>

typealias Table<T> = Map<Id, T>

enum class ShapeFunction {
  polygon
}

data class RgbStroke(
    val width: Float,
    val color: Vector3
)

data class GrayscaleStroke(
    val width: Float,
    val value: Float
)

data class Shapes(
    val ids: List<Id>,
    val grayscaleFills: Table<Float>,
    val rgbFills: Table<Vector3>,
    val functions: Table<ShapeFunction>,
    val pointLists: Table<ShapePoints>,
    val grayscaleStrokes: Table<GrayscaleStroke>,
    val rgbStrokes: Table<RgbStroke>,
    val transforms: Table<Matrix3>
)

fun newShapes() =
    Shapes(
        ids = listOf(),
        grayscaleFills = mapOf(),
        rgbFills = mapOf(),
        functions = mapOf(),
        pointLists = mapOf(),
        rgbStrokes = mapOf(),
        grayscaleStrokes = mapOf(),
        transforms = mapOf()
    )

fun mergeShapes(first: Shapes, second: Shapes): Shapes =
    Shapes(
        ids = first.ids.plus(second.ids),
        grayscaleFills = first.grayscaleFills.plus(second.grayscaleFills),
        rgbFills = first.rgbFills.plus(second.rgbFills),
        functions = first.functions.plus(second.functions),
        pointLists = first.pointLists.plus(second.pointLists),
        rgbStrokes = first.rgbStrokes.plus(second.rgbStrokes),
        grayscaleStrokes = first.grayscaleStrokes.plus(second.grayscaleStrokes),
        transforms = first.transforms.plus(second.transforms)
    )
