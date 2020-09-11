package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

const val clipBoundsKey = "silentorb.bloom.clipBoxKey"

data class SimpleBox(
    val name: String = "",
    val dimensions: Vector2i,
    val boxes: List<OffsetBox> = listOf(),
    val depiction: Depiction? = null,
    val attributes: Map<String, Any?> = mapOf()
)

data class OffsetBox(
    val child: SimpleBox,
    val offset: Vector2i
)

data class Box(
    val name: String = "",
    val bounds: Bounds,
    val boxes: List<Box> = listOf(),
    val depiction: Depiction? = null,
    val attributes: Map<String, Any?> = mapOf()
) {
  val dimensions: Vector2i get() = bounds.dimensions
}

fun toBox(box: SimpleBox, offset: Vector2i = Vector2i.zero): Box =
    Box(
        name = box.name,
        bounds = Bounds(dimensions = box.dimensions, position = offset),
        boxes = box.boxes.map { (child, offset) -> toBox(child, offset) },
        depiction = box.depiction,
        attributes = box.attributes
    )

typealias Boxes = Collection<Box>

typealias Seed = Vector2i
typealias Flower = (Seed) -> Box
typealias WildFlower = (Box) -> Box
typealias SimpleFlower = (Vector2i) -> SimpleBox
typealias SimpleLengthFlower<T> = (Int) -> SimpleBox

typealias LengthFlower<T> = (Int) -> Box
typealias LengthFlowerWrapper<T> = (LengthFlower<T>) -> LengthFlower<T>
typealias HorizontalLengthFlower = LengthFlower<HorizontalPlane>
typealias VerticalLengthFlower = LengthFlower<VerticalPlane>

typealias FlowerWrapper = (Flower) -> Flower
typealias IndexedFlowerWrapper = (Int, Flower) -> Flower

typealias ForwardLayout = (Vector2i) -> Bounds

typealias ReverseLayout = (Vector2i, Bounds, Bounds) -> Bounds

fun toFlower(flower: SimpleFlower): Flower = { dimensions ->
  toBox(flower(dimensions))
}
