package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

val emptyBoxList: List<Box> = listOf()

const val clipBoundsKey = "silentorb.bloom.clipBoxKey"

data class SimpleBox(
    val name: String = "",
    val dimensions: Vector2i,
    val boxes: List<Box> = emptyBoxList,
    val depiction: Depiction? = null,
    val attributes: Map<String, Any?> = mapOf()
)

data class Box(
    val name: String = "",
    val bounds: Bounds,
    val boxes: List<Box> = emptyBoxList,
    val depiction: Depiction? = null,
    val attributes: Map<String, Any?> = mapOf()
) {
  val dimensions: Vector2i get() = bounds.dimensions
}

fun toBox(box: SimpleBox) =
    Box(
        name = box.name,
        bounds = Bounds(dimensions = box.dimensions),
        boxes = box.boxes,
        depiction = box.depiction,
        attributes = box.attributes
    )

typealias Boxes = Collection<Box>

typealias Seed = Vector2i
typealias Flower = (Seed) -> Box
typealias WildFlower = (Box) -> Box

typealias LengthFlower<T> = (Int) -> Box
typealias LengthFlowerWrapper<T> = (LengthFlower<T>) -> LengthFlower<T>
typealias HorizontalLengthFlower = LengthFlower<HorizontalPlane>
typealias VerticalLengthFlower = LengthFlower<VerticalPlane>

typealias FlowerWrapper = (Flower) -> Flower
typealias IndexedFlowerWrapper = (Int, Flower) -> Flower

typealias ForwardLayout = (Vector2i) -> Bounds

typealias ReverseLayout = (Vector2i, Bounds, Bounds) -> Bounds
