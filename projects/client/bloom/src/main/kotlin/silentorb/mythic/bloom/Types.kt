package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

const val clipBoundsKey = "silentorb.bloom.clipBoxKey"

interface DimensionBox {
  val dimensions: Vector2i
}

interface AttributeHolder {
  val attributes: Map<String, Any?>
}

data class Box(
    val name: String = "",
    override val dimensions: Vector2i,
    val boxes: List<OffsetBox> = listOf(),
    val depiction: Depiction? = null,
    override val attributes: Map<String, Any?> = mapOf()
) : DimensionBox, AttributeHolder

data class OffsetBox(
    val child: Box,
    val offset: Vector2i = Vector2i.zero
) : AttributeHolder {
  val name: String get() = child.name
  val dimensions: Vector2i get() = child.dimensions
  val boxes: List<OffsetBox> get() = child.boxes
  val depiction: Depiction? get() = child.depiction
  override val attributes: Map<String, Any?> get() = child.attributes
  val bounds: Bounds get() = Bounds(position = offset, dimensions = dimensions)
}

//data class Box(
//    val name: String = "",
//    val bounds: Bounds,
//    val boxes: List<Box> = listOf(),
//    val depiction: Depiction? = null,
//    val attributes: Map<String, Any?> = mapOf()
//) : DimensionBox {
//  override val dimensions: Vector2i get() = bounds.dimensions
//}

//fun toBox(box: Box, offset: Vector2i = Vector2i.zero): Box =
//    Box(
//        name = box.name,
//        bounds = Bounds(dimensions = box.dimensions, position = offset),
//        boxes = box.boxes.map { (child, offset) -> toBox(child, offset) },
//        depiction = box.depiction,
//        attributes = box.attributes
//    )

typealias Seed = Vector2i
typealias Flower = (Seed) -> Box
typealias WildFlower = (Box) -> Box
typealias SimpleWildFlower = (Box) -> Box
typealias SimpleFlower = (Vector2i) -> Box
typealias SimpleLengthFlower = (Int) -> Box

typealias LengthFlower = (Int) -> Box

typealias FlowerWrapper = (Flower) -> Flower
typealias IndexedFlowerWrapper = (Int, Flower) -> Flower

//typealias ForwardLayout = (Vector2i) -> Vector2i
//
//typealias ReverseLayout = (Vector2i, Vector2i, Vector2i) -> Bounds

//fun toFlower(flower: SimpleFlower): Flower = { dimensions ->
//  toBox(flower(dimensions))
//}
