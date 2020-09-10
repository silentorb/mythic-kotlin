package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

val emptyBoxList: List<Box> = listOf()

const val clipBoundsKey = "silentorb.bloom.clipBoxKey"

data class Box(
    val name: String = "",
    val bounds: Bounds,
    val boxes: List<Box> = emptyBoxList,
    val depiction: Depiction? = null,
    val attributes: Map<String, Any?> = mapOf()
)

typealias Boxes = Collection<Box>

typealias Seed = Vector2i
typealias Flower = (Seed) -> Box
typealias WildFlower = (Box) -> Box

typealias FlowerWrapper = (Flower) -> Flower
typealias IndexedFlowerWrapper = (Int, Flower) -> Flower

typealias ForwardLayout = (Vector2i) -> Bounds

typealias ReverseLayout = (Vector2i, Bounds, Bounds) -> Bounds
