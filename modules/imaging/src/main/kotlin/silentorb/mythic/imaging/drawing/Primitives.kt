package silentorb.mythic.imaging.drawing

import silentorb.mythic.spatial.Vector2

fun newRectangle(dimensions: Vector2): Shapes {
  val id = 1
  return newShapes()
      .copy(
          ids = listOf(id),
          functions = mapOf(id to ShapeFunction.polygon),
          pointLists = mapOf(
              id to listOf(
                  Vector2(0f, 0f),
                  Vector2(dimensions.x, 0f),
                  Vector2(dimensions.x, dimensions.y),
                  Vector2(0f, dimensions.y)
              )
          )
      )
}
