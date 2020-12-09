package silentorb.mythic.bloom

import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector2i

const val clipBoundsKey = "silentorb.bloom.clipBoxKey"

interface DimensionBox {
  val dimensions: Vector2i
}

interface AttributeHolder {
  val attributes: Map<String, Any?>
  val handler: InputHandler?
}

data class Input(
    val commands: Commands,
    val mousePosition: Vector2i,
)

typealias InputHandler = (Input, Bounds) -> Commands

fun combineHandlers(vararg handlers: InputHandler): InputHandler =
    { input, bounds ->
      handlers.flatMap { it(input, bounds) }
    }

data class Box(
    val name: String = "",
    override val dimensions: Vector2i,
    val boxes: List<OffsetBox> = listOf(),
    val depiction: Depiction? = null,
    override val handler: InputHandler? = null,
    override val attributes: Map<String, Any?> = mapOf()
) : DimensionBox, AttributeHolder {

  fun addAttributes(vararg attributes: Pair<String, Any?>): Box =
      this.copy(
          attributes = this.attributes + attributes
      )

  infix fun handle(handler: InputHandler): Box {
    val nextHandler = if (this.handler != null)
      combineHandlers(this.handler, handler)
    else
      handler

    return this.copy(
        handler = nextHandler
    )
  }

  fun toFlower(): Flower = { this }
}

data class OffsetBox(
    val child: Box,
    val offset: Vector2i = Vector2i.zero
) : AttributeHolder {
  val name: String get() = child.name
  val dimensions: Vector2i get() = child.dimensions
  val boxes: List<OffsetBox> get() = child.boxes
  val depiction: Depiction? get() = child.depiction
  override val attributes: Map<String, Any?> get() = child.attributes
  override val handler: InputHandler? get() = child.handler
  val bounds: Bounds get() = Bounds(position = offset, dimensions = dimensions)
}

typealias Seed = Vector2i
typealias Flower = (Seed) -> Box
typealias WildFlower = (Box) -> Box
typealias LengthFlower = (Int) -> Box
typealias BoxSource<T> = (T) -> Box
