package silentorb.mythic.editing

import imgui.ImGui
import imgui.flag.ImGuiKey
import org.lwjgl.glfw.GLFW
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.transformToScreenIncludingBehind

fun getActiveEditorGraphId(editor: Editor): Id? {
  val graph = editor.state.graph
  return if (editor.graphLibrary.containsKey(graph))
    graph
  else
    editor.graphLibrary.keys.firstOrNull()
}

fun getActiveEditorGraph(editor: Editor): Graph? =
    editor.staging ?: editor.graph ?: editor.graphLibrary[editor.state.graph]

fun defaultEditorState() =
    EditorState(
        cameras = mapOf(defaultViewportId to CameraRig(location = Vector3(-10f, 0f, 0f))),
    )

fun isAltDown(): Boolean =
    ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT)

fun isShiftDown(): Boolean =
    ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)

fun mapKey(key: String): Int =
    when {
      key.length == 1 -> key.first().toInt()
      // Currently only handling numeric keypad keys
      key == "Del" -> GLFW.GLFW_KEY_DELETE
      key == numpadPeriodKey -> GLFW.GLFW_KEY_KP_DECIMAL
      key.contains(keypadKey) -> key.last().toInt() - '0'.toInt() + GLFW.GLFW_KEY_KP_0
      else -> throw Error("Keystroke type yet supported: $key")
    }

fun isShortcutPressed(shortcut: String): Boolean {
  val parts = shortcut.split("+")
  val key = parts.last()
  val leftModifiers = parts.dropLast(1)
      .map { modifierName ->
        when (modifierName) {
          "Ctrl" -> GLFW.GLFW_KEY_LEFT_CONTROL
          "Shift" -> GLFW.GLFW_KEY_LEFT_SHIFT
          "Alt" -> GLFW.GLFW_KEY_LEFT_ALT
          else -> throw Error("Not supported")
        }
      }

  val keyIndex = mapKey(key)

  val isKeyPressed = ImGui.isKeyPressed(keyIndex)
  val modifiersArePressed = leftModifiers.all { leftModifier ->
    ImGui.isKeyDown(leftModifier) || ImGui.isKeyDown(leftModifier + 4)
  }
  return isKeyPressed && modifiersArePressed
}

fun getTransform(graph: Graph, node: Id): Matrix {
  val translation = getValue<Vector3>(graph, node, Properties.translation) ?: Vector3.zero
  val rotation = getValue<Vector3>(graph, node, Properties.rotation) ?: Vector3.zero
  val scale = getValue<Vector3>(graph, node, Properties.scale) ?: Vector3.unit
  val localTransform = Matrix.identity
      .translate(translation)
      .rotateZ(rotation.z)
      .rotateY(rotation.y)
      .rotateX(rotation.x)
      .scale(scale)

  val parent = getValue<Id>(graph, node, Properties.parent)
  return if (parent != null)
    getTransform(graph, parent) * localTransform
  else
    localTransform
}

fun isEscapePressed(): Boolean =
    ImGui.isKeyPressed(ImGui.getKeyIndex(ImGuiKey.Escape))

fun axisMask(axis: Set<Axis>): List<Float> =
    (0 until 3).map { index ->
      if (axis.any { it.ordinal == index })
        1f
      else
        0f
    }

fun transformPoint(transform: Matrix, dimensions: Vector2, offset: Vector2): ScreenTransform = { point ->
  val sample = transformToScreenIncludingBehind(transform, point)
//  sample * Vector2(1f, -2f) * dimensions + offset
  Vector2(sample.x + 1f, 1f - sample.y) / 2f * dimensions + offset
}

tailrec fun gatherChildren(graph: Graph, nodes: Set<Id>, accumulator: Set<Id> = setOf()): Set<Id> {
  val next = nodes
      .flatMap { node ->
        graph.filter { it.property == Properties.parent && it.target == node }
      }
      .map { it.source }
      .toSet()

  val nextAccumulator = accumulator + nodes
  return if (next.none())
    nextAccumulator
  else
    gatherChildren(graph, next, nextAccumulator)
}
