package silentorb.mythic.editing

import imgui.ImGui
import imgui.flag.ImGuiKey
import org.lwjgl.glfw.GLFW
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.GraphLibrary
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.getValue
import silentorb.mythic.scenery.Properties
import silentorb.mythic.spatial.*

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

fun getTransform(graph: Graph, node: Key): Matrix {
  val translation = getValue<Vector3>(graph, node, Properties.translation) ?: Vector3.zero
  val rotation = getValue<Vector3>(graph, node, Properties.rotation) ?: Vector3.zero
  val scale = getValue<Vector3>(graph, node, Properties.scale) ?: Vector3.unit
  val localTransform = Matrix.identity
      .translate(translation)
      .rotateZ(rotation.z)
      .rotateY(rotation.y)
      .rotateX(rotation.x)
      .scale(scale)

  val parent = getValue<Key>(graph, node, Properties.parent)
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

tailrec fun gatherChildren(graph: Graph, nodes: Set<Key>, accumulator: Set<Key> = setOf()): Set<Key> {
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

fun sceneFileNameWithoutExtension(fileName: String): String =
    fileName.replace(sceneFileExtension, "")

fun isSceneFile(fileName: String): Boolean =
    fileName.substring(fileName.length - sceneFileExtension.length) == sceneFileExtension

fun getSceneFiles(editor: Editor): Sequence<FileItem> =
    editor.fileItems
        .values
        .asSequence()
        .filter { it.type == FileItemType.file && isSceneFile(it.name) }

// This function will recursively scans loaded graphs but does no graph loading
// so it does not recursively scan unloaded graphs.
// It's assumed that this function will be called multiple times between loading, where each
// pass includes more missing graphs until the unloaded set is loaded
tailrec fun getGraphDependencies(
    graphLibrary: GraphLibrary,
    graphs: Set<Key>,
    accumulator: Set<Key> = setOf()
): Set<Key> =
    if (graphs.none())
      accumulator
    else {
      val dependencies = graphs
          .flatMap { graphId ->
            val graph = graphLibrary[graphId]
            if (graph == null)
              listOf()
            else
              graph
                  .filter { it.property == Properties.type }
                  .map { it.target as Key }
          }
          .toSet()

      val nextGraphs = dependencies - accumulator
      val nextAccumulator = accumulator + dependencies

      getGraphDependencies(graphLibrary, nextGraphs, nextAccumulator)
    }

fun hexColorStringToVector4(value: String): Vector4 {
  assert(value.length == 9)
  val red = value.substring(1, 3).toInt(16).toFloat()
  val green = value.substring(3, 5).toInt(16).toFloat()
  val blue = value.substring(5, 7).toInt(16).toFloat()
  val alpha = value.substring(7, 9).toInt(16).toFloat()
  return Vector4(red, green, blue, alpha) / 255f
}

//fun vector4toHexColorString(value: Vector4): String {
//  val temp = (value * 255f).toVector4i()
//  val red = temp.x shl 32
//  val green = temp.y shl 16
//  val blue = temp.z shl 8
//  val alpha = temp.w shl 0
//  return red + green + blue + alpha
//}

fun arrayToHexColorString(values: FloatArray): String {
  val red = Integer.toHexString((values[0] * 255f).toInt())
  val green = Integer.toHexString((values[1] * 255f).toInt())
  val blue = Integer.toHexString((values[2] * 255f).toInt())
  val alpha = Integer.toHexString((values[3] * 255f).toInt())
  return "#$red$green$blue$alpha"
}
