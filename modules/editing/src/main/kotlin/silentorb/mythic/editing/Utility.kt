package silentorb.mythic.editing

import imgui.ImGui
import imgui.flag.ImGuiKey
import org.lwjgl.glfw.GLFW
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3

fun getActiveEditorGraphId(editor: Editor): Id? {
  val graph = editor.state.graph
  return if (editor.graphLibrary.containsKey(graph))
    graph
  else
    editor.graphLibrary.keys.firstOrNull()
}

fun getActiveEditorGraph(editor: Editor): Graph? =
    editor.graphLibrary[getActiveEditorGraphId(editor)]

fun defaultEditorState() =
    EditorState(
        cameras = mapOf(defaultViewportId to CameraRig(location = Vector3(-10f, 0f, 0f))),
    )

fun isAltDown(): Boolean =
    ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT)

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

  val keyIndex = key.first().toInt()
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