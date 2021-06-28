package silentorb.mythic.bloom

import silentorb.mythic.bloom.old.isInBounds
import silentorb.mythic.drawing.Canvas
import silentorb.mythic.haft.DeviceIndexes
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.happenings.Commands
import silentorb.mythic.platforming.Devices
import silentorb.mythic.platforming.InputEvent
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.toVector2i

const val clipBoundsKey = "silentorb.bloom.clipBoxKey"

interface DimensionBox {
  val dimensions: Vector2i
}

interface AttributeHolder {
  val attributes: Map<String, Any?>
  val handler: InputHandler?
}

data class Input(
    val commands: Commands, // Deprecated
    val mousePosition: Vector2i,
)

typealias InputHandler = (Input, Bounds) -> Commands

fun combineHandlers(vararg handlers: InputHandler): InputHandler =
    { input, bounds ->
      handlers.flatMap { it(input, bounds) }
    }

val isLeftMouseDownEvent: (InputEvent) -> Boolean = { event ->
  event.device == Devices.mouse && event.index == 0
}

data class LogicInput(
    val state: BloomState,
    val deviceStates: List<InputDeviceState>,
    val mousePosition: Vector2i = deviceStates.last().mousePosition.toVector2i(),
) {
  fun isMouseOver(box: OffsetBox): Boolean =
      isInBounds(mousePosition, box)

  fun isPressed(device: Int, index: Int): Boolean =
      deviceStates.last().events.none { it.device == device && it.index == index } &&
          deviceStates.first().events.any { it.device == device && it.index == index }

  val isLeftMouseDownStarted: Boolean
    get() =
      deviceStates.last().events.any(isLeftMouseDownEvent) &&
          deviceStates.first().events.none(isLeftMouseDownEvent)

  val isLeftMouseClick: Boolean
    get() = isPressed(DeviceIndexes.mouse, 0)

  val isLeftMouseDown: Boolean
    get() =
      deviceStates.last().events.any(isLeftMouseDownEvent)
}

typealias LogicModule = (LogicInput, OffsetBox) -> BloomState

data class Box(
    val name: String = "",
    override val dimensions: Vector2i,
    val boxes: List<OffsetBox> = listOf(),
    val depiction: Depiction? = null,
    val logic: LogicModule? = null,
    override val handler: InputHandler? = null,
    override val attributes: Map<String, Any?> = mapOf()
) : DimensionBox, AttributeHolder {

  fun addAttributes(vararg attributes: Pair<String, Any?>): Box =
      this.copy(
          attributes = this.attributes + attributes
      )

  fun addAttributes(attributes: Map<String, Any?>): Box =
      this.copy(
          attributes = this.attributes + attributes
      )

  fun addLogic(logic: LogicModule): Box =
      this.copy(
          logic = if (this.logic != null)
            composeLogic(this.logic, logic)
          else
            logic
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

fun withAttributes(vararg attributes: Pair<String, Any?>): (Flower) -> Flower = { flower ->
  { seed ->
    flower(seed)
        .addAttributes(*attributes)
  }
}

fun withAttributes(attributes: Map<String, Any?>): (Flower) -> Flower = { flower ->
  { seed ->
    flower(seed)
        .addAttributes(attributes)
  }
}

fun withLogic(logic: LogicModule): (Flower) -> Flower = { flower ->
  { seed ->
    flower(seed)
        .addLogic(logic)
  }
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

typealias BloomState = StateBag
//data class BloomState(
//    val ephemeral: StateBag = mapOf(),
//    val persistent: StateBag = mapOf(),
//) {
//  operator fun plus(value: BloomState): BloomState =
//      BloomState(
//          ephemeral = this.ephemeral + value.ephemeral,
//          persistent = this.persistent + value.persistent,
//      )
//
//  companion object {
//    val empty = BloomState()
//  }
//}

data class Seed(
    val dimensions: Vector2i,
    val state: BloomState = mapOf(),
    val previousState: BloomState = mapOf(),
)

typealias Flower = (Seed) -> Box
typealias WildFlower = (Box) -> Flower
typealias LengthFlower = (Int) -> Box
typealias BoxSource<T> = (T) -> Box

typealias Depiction = (Bounds, Canvas) -> Unit
typealias StateBag = Map<String, Any>
typealias StateBagMods = StateBag?
