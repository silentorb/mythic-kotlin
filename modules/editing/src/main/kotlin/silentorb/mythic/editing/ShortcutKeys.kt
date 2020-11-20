package silentorb.mythic.editing

import imgui.ImGui
import imgui.flag.ImGuiKey
import org.lwjgl.glfw.GLFW

object ModifierKeys {
  const val ctrl = 1
  const val alt = 2
  const val shift = 4
}

private var modifierStateCtrl: Boolean = false
private var modifierStateShift: Boolean = false
private var modifierStateAlt: Boolean = false
private var modifierStateComposite: Int = 0

fun updateModifierKeyStates() {
  modifierStateCtrl = ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)
  modifierStateShift = ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)
  modifierStateAlt = ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT)
  modifierStateComposite = (if (modifierStateCtrl) ModifierKeys.ctrl else 0) +
      (if (modifierStateShift) ModifierKeys.shift else 0) +
      (if (modifierStateAlt) ModifierKeys.alt else 0)
}

fun isCtrlDown(): Boolean =
    modifierStateCtrl

fun isAltDown(): Boolean =
    modifierStateAlt

fun isShiftDown(): Boolean =
    modifierStateShift

fun getCompositeModifierKeys(): Int =
    modifierStateComposite

val functionKeyPattern = Regex("^F(\\d\\d?)$")

fun mapKey(key: String): Int =
    when {
      key.length == 1 -> key.first().toInt()
      // Currently only handling numeric keypad keys
      key == "Del" -> GLFW.GLFW_KEY_DELETE
      key == numpadPeriodKey -> GLFW.GLFW_KEY_KP_DECIMAL
      functionKeyPattern.matches(key) -> {
        println()
        GLFW.GLFW_KEY_F1 + functionKeyPattern.matchEntire(key)!!.groups.last()!!.value.toInt() - 1
      }
      key.contains(keypadKey) -> key.last().toInt() - '0'.toInt() + GLFW.GLFW_KEY_KP_0
      else -> throw Error("Keystroke type yet supported: $key")
    }

// Eventually this workflow could be optimized so that shortcuts are translated entirely to single integers
// then those values can be directly compared or even used as keys in a map.
// Also, the shortcut key code is no longer as tightly coupled to menus and could be handled more
// cleanly outside of them.
fun isShortcutPressed(shortcut: String): Boolean {
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

  val isKeyPressed = ImGui.isKeyPressed(keyIndex)
  val modifiersArePressed = requiredModifiers == getCompositeModifierKeys()
  return isKeyPressed && modifiersArePressed
}

fun isEscapePressed(): Boolean =
    ImGui.isKeyPressed(ImGui.getKeyIndex(ImGuiKey.Escape))
