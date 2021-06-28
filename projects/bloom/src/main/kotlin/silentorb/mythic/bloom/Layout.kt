package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i
import kotlin.math.max

typealias BloomKey = String

typealias Aligner = (Int, Int) -> Int

val centered: Aligner = { parent, child ->
  max(0, (parent - child) / 2)
}

val justifiedStart: Aligner = { _, _ ->
  0
}

val justifiedEnd: Aligner = { parent, child ->
  parent - child
}

fun percentage(value: Float): Aligner = { parent, _ ->
  (parent.toFloat() * value).toInt()
}

fun marginSingle(plane: Plane, all: Int = 0, left: Int = all, top: Int = all, bottom: Int = all, right: Int = all): (LengthFlower) -> LengthFlower = { child ->
  { length ->
    val sizeOffset = Vector2i(left + right, top + bottom)
    val relativeSizeOffset = plane(sizeOffset)
    val box = child(length - relativeSizeOffset.x)
    Box(
        dimensions = box.dimensions + sizeOffset,
        boxes = listOf(
            OffsetBox(
                child = box,
                offset = Vector2i(left, top)
            )
        )
    )
  }
}

fun boxMargin(all: Int = 0, left: Int = all, top: Int = all, bottom: Int = all, right: Int = all): (Box) -> Box = { box ->
  val sizeOffset = Vector2i(left + right, top + bottom)
  Box(
      dimensions = box.dimensions + sizeOffset,
      boxes = listOf(
          OffsetBox(
              child = box,
              offset = Vector2i(left, top)
          )
      )
  )
}

fun flowerMargin(all: Int = 0, left: Int = all, top: Int = all, bottom: Int = all, right: Int = all): (Flower) -> Flower = { flower ->
  val sizeOffset = Vector2i(left + right, top + bottom)
  ;{ seed ->
  val child = flower(seed.copy(dimensions = seed.dimensions - sizeOffset))
  Box(
      dimensions = child.dimensions + sizeOffset,
      boxes = listOf(
          OffsetBox(
              child = child,
              offset = Vector2i(left, top)
          )
      )
  )
}
}

fun centered(box: Box): Flower = { seed ->
  val offset = Vector2i(
      centered(seed.dimensions.x, box.dimensions.x),
      centered(seed.dimensions.y, box.dimensions.y)
  )
  Box(
      dimensions = Vector2i(max(seed.dimensions.x, box.dimensions.x), max(seed.dimensions.y, box.dimensions.y)),
      boxes = listOf(OffsetBox(box, offset))
  )
}

fun centeredFlower(flower: Flower): Flower = { seed ->
  val box = flower(seed)
  val offset = Vector2i(
      centered(seed.dimensions.x, box.dimensions.x),
      centered(seed.dimensions.y, box.dimensions.y)
  )
  Box(
      dimensions = Vector2i(max(seed.dimensions.x, box.dimensions.x), max(seed.dimensions.y, box.dimensions.y)),
      boxes = listOf(OffsetBox(box, offset))
  )
}

fun alignSingleFlower(aligner: Aligner, plane: Plane, content: Flower): Flower = { seed ->
  val parent = plane(seed.dimensions)
  val box = content(seed.copy(dimensions = parent))
  val child = plane(box.dimensions)
  val offset = plane(
      Vector2i(
          aligner(parent.x, child.x),
          0
      )
  )

  Box(
      dimensions = plane(Vector2i(plane(seed.dimensions).x, plane(box.dimensions).y)),
      boxes = listOf(OffsetBox(box, offset))
  )
}

fun alignSingle(aligner: Aligner, plane: Plane, lengthFlower: LengthFlower): Flower = { seed ->
  val parent = plane(seed.dimensions)
  val box = lengthFlower(parent.y)
  val child = plane(box.dimensions)
  val offset = plane(
      Vector2i(
          aligner(parent.x, child.x),
          0
      )
  )

  Box(
      dimensions = seed.dimensions,
      boxes = listOf(OffsetBox(box, offset))
  )
}

fun alignSingle(aligner: Aligner, plane: Plane, box: Box): LengthFlower = { length ->
  val child = plane(box.dimensions)
  val offset = plane(
      Vector2i(
          aligner(length, child.x),
          0
      )
  )

  val finalLength = max(length, child.x + plane(offset).x)
  Box(
      dimensions = plane(Vector2i(finalLength, child.y)),
      boxes = listOf(OffsetBox(box, offset))
  )
}

fun alignBoth(horizontal: Aligner, vertical: Aligner, flower: Flower): Flower =
    { seed ->
      val box = flower(seed)
      val offset = Vector2i(
          horizontal(seed.dimensions.x, box.dimensions.x),
          vertical(seed.dimensions.y, box.dimensions.y)
      )

      // Currently doesn't support negative numbers
      assert(offset.x >= 0)
      assert(offset.y >= 0)

      Box(
          dimensions = box.dimensions + offset,
          boxes = listOf(OffsetBox(box, offset))
      )
    }

fun alignBoth(horizontal: Aligner, vertical: Aligner, box: Box): Flower =
    alignBoth(horizontal, vertical, box.toFlower())

fun alignBoth(both: Aligner, box: Box): Flower =
    alignBoth(both, both, box)
