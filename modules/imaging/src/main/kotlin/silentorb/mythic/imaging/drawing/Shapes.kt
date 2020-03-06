package silentorb.mythic.imaging.drawing

import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3

typealias Id = Int

typealias ShapePoints = List<Vector2>

typealias Table<T> = Map<Id, T>

enum class ShapeFunction {
  rectangle
}

data class Stroke(
    val width: Float,
    val color: Vector3
)

data class Shapes(
    val ids: List<Id>,
    val dimensions: Table<Vector2>,
    val flatRgbFills: Table<Vector3>,
    val functions: Table<ShapeFunction>,
    val pointLists: Table<ShapePoints>,
    val strokes: Table<Stroke>
)

fun newShapes() =
    Shapes(
        ids = listOf(),
        dimensions = mapOf(),
        flatRgbFills = mapOf(),
        functions = mapOf(),
        pointLists = mapOf(),
        strokes = mapOf()
    )

fun mergeShapes(first: Shapes, second: Shapes): Shapes =
    Shapes(
        ids = first.ids.plus(second.ids),
        dimensions = first.dimensions.plus(second.dimensions),
        flatRgbFills = first.flatRgbFills.plus(second.flatRgbFills),
        functions = first.functions.plus(second.functions),
        pointLists = first.pointLists.plus(second.pointLists),
        strokes = first.strokes.plus(second.strokes)
    )
