package silentorb.mythic.editing

import imgui.ImGui
import imgui.flag.ImGuiKey
import org.lwjgl.glfw.GLFW
import silentorb.mythic.editing.panels.defaultViewportId
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.GraphLibrary
import silentorb.mythic.ent.Key
import silentorb.mythic.scenery.SceneProperties
import silentorb.mythic.spatial.*

fun getLatestSnapshot(editor: Editor): Snapshot? =
    editor.history[editor.state.graph]?.pastAndPresent?.lastOrNull()

fun getPreviousSnapshot(editor: Editor): Snapshot? =
    editor.history[editor.state.graph]?.pastAndPresent?.dropLast(1)?.lastOrNull()

fun getNextSnapshot(editor: Editor): Snapshot? =
    editor.history[editor.state.graph]?.future?.firstOrNull()

fun getLatestGraph(editor: Editor): Graph? =
    getLatestSnapshot(editor)?.graph

fun getActiveEditorGraph(editor: Editor): Graph? =
    editor.staging ?: getLatestGraph(editor) ?: editor.graphLibrary[editor.state.graph]

fun defaultEditorState() =
    EditorState(
        cameras = mapOf(defaultViewportId to CameraRig(location = Vector3(-10f, 0f, 0f))),
    )

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

fun mapKey(key: String): Int =
    when {
      key.length == 1 -> key.first().toInt()
      // Currently only handling numeric keypad keys
      key == "Del" -> GLFW.GLFW_KEY_DELETE
      key == numpadPeriodKey -> GLFW.GLFW_KEY_KP_DECIMAL
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
                  .filter { it.property == SceneProperties.instance }
                  .map { it.target as Key }
          }
          .toSet()

      val nextGraphs = dependencies - accumulator
      val nextAccumulator = accumulator + dependencies

      getGraphDependencies(graphLibrary, nextGraphs, nextAccumulator)
    }
