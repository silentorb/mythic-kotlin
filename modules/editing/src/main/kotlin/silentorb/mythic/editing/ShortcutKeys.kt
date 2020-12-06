package silentorb.mythic.editing

import imgui.ImGui
import imgui.flag.ImGuiKey
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
import silentorb.mythic.ent.singleValueCache
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.platforming.Devices
import silentorb.mythic.platforming.InputEvent
import silentorb.mythic.platforming.Devices.keyboard

object ModifierKeys {
  const val ctrl = 1 shl 10
  const val alt = 1 shl 11
  const val shift = 1 shl 12
}

private var modifierStateCtrl: Boolean = false
private var modifierStateShift: Boolean = false
private var modifierStateAlt: Boolean = false
private var modifierStateComposite: Int = 0
private val mouseButtonState: MutableList<Boolean> = MutableList(3) { false }
private var doubleClickState: Boolean = false

fun updateModifierKeyStates() {
  modifierStateCtrl = ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)
  modifierStateShift = ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)
  modifierStateAlt = ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT)
  modifierStateComposite =
      (if (modifierStateCtrl) ModifierKeys.ctrl else 0) +
          (if (modifierStateShift) ModifierKeys.shift else 0) +
          (if (modifierStateAlt) ModifierKeys.alt else 0)

  for (i in 0 until 3) {
    mouseButtonState[i] = ImGui.isMouseDown(i)
  }

  doubleClickState = ImGui.isMouseDoubleClicked(0)
}

fun isCtrlDown(): Boolean =
    modifierStateCtrl

fun isAltDown(): Boolean =
    modifierStateAlt

fun isShiftDown(): Boolean =
    modifierStateShift

fun getCompositeModifierKeys(): Int =
    modifierStateComposite

fun isMouseDown(index: Int): Boolean =
    mouseButtonState[index]

fun isDoubleClick(): Boolean =
    doubleClickState

val functionKeyPattern = Regex("^F(\\d\\d?)$")

fun mapKey(key: String): Int =
    when {
      key.length == 1 -> key.first().toInt()
      // Currently only handling numeric keypad keys
      key == "Del" -> GLFW.GLFW_KEY_DELETE
      key == numpadPeriodKey -> GLFW.GLFW_KEY_KP_DECIMAL
      functionKeyPattern.matches(key) -> {
        GLFW.GLFW_KEY_F1 + functionKeyPattern.matchEntire(key)!!.groups.last()!!.value.toInt() - 1
      }
      key.contains(keypadKey) -> key.last().toInt() - '0'.toInt() + GLFW.GLFW_KEY_KP_0
      else -> throw Error("Keystroke type yet supported: $key")
    }

fun compressShortcut(shortcut: String): Int {
  val parts = shortcut.split("+")
  val key = parts.last()
  val requiredModifiers = parts.dropLast(1)
      .map { modifierName ->
        when (modifierName) {
          "Ctrl" -> ModifierKeys.ctrl
          "Shift" -> ModifierKeys.shift
          "Alt" -> ModifierKeys.alt
          else -> throw Error("Not supported")
        }
      }
      .fold(0) { a, b -> a or b }

  val keyIndex = mapKey(key)
  return keyIndex or requiredModifiers
}

fun compressBindings(bindings: KeystrokeBindings): CompressedKeystrokeBindings =
    bindings
        .entries
        .groupBy { it.value }
        .mapValues { group ->
          group.value.map { (contextCommand, _) ->
            contextCommand
          }
        }
        .mapKeys { compressShortcut(it.key) }

fun isEscapePressed(): Boolean =
    ImGui.isKeyPressed(ImGui.getKeyIndex(ImGuiKey.Escape))

fun getKeypresses(deviceStates: List<InputDeviceState>): List<InputEvent> =
    if (deviceStates.size < 2)
      listOf()
    else {
      val (previous, next) = deviceStates.takeLast(2)
      next.events
          .filter { event ->
            event.device == Devices.keyboard &&
                previous.events.none { it.device == Devices.keyboard && it.index == event.index }
          }
    }

fun getPressedShortcut(keyPresses: List<InputEvent>): Int? {
  val key = keyPresses.firstOrNull()
  return if (key == null)
    null
  else {
    key.index or getCompositeModifierKeys()
  }
}

val getCompressedBindings = singleValueCache(::compressBindings)

fun getShortcutCommands(bindings: KeystrokeBindings, context: String, deviceStates: List<InputDeviceState>): Commands {
  val keyPresses = getKeypresses(deviceStates)
      .filter { it.index < GLFW_KEY_LEFT_SHIFT }

  val compressedBindings = getCompressedBindings(bindings)
  val combo = getPressedShortcut(keyPresses)
  return if (combo == null)
    listOf()
  else {
    val options = compressedBindings[combo]
    val commandType = if (options != null && options.any()) {
      val option = if (options.size == 1)
        options.first()
      else
        options.firstOrNull { it.context == context }

      option?.command
    } else
      null

    return if (commandType != null)
      listOf(Command(commandType))
    else
      listOf()
  }
}
