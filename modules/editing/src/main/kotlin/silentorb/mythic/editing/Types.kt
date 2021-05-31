package silentorb.mythic.editing

import imgui.ImDrawList
import silentorb.mythic.ent.*
import silentorb.mythic.ent.scenery.Expanders
import silentorb.mythic.lookinglass.ResourceInfo
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.lookinglass.ElementGroup
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector4i
import java.nio.file.Path

const val sceneFileExtension = ".scene"

typealias SceneTree = Map<Key, Key>
typealias MenuResponse = Commands
typealias MenuDefinition = (MenuChannel) -> MenuResponse
typealias PanelResponse = Pair<String?, Commands>

data class Typeface(
    val name: String,
    val path: String,
    val size: Float
)

object DraggingTypes {
  const val file = "file"
  const val folder = "folder"
  const val node = "node"
}

typealias DefaultValueSource = (Editor) -> Any?
typealias PropertyWidget = (Editor, Entry, String) -> Any?

data class PropertyDefinition(
    val displayName: String,
    val serialization: Serialization? = null,
    val widget: PropertyWidget?,
    val dependencies: Set<Key> = setOf(),
    val defaultValue: DefaultValueSource? = null,
)

typealias PropertyDefinitions = Map<Key, PropertyDefinition>
typealias NodeSelection = Set<Key>

data class Snapshot(
    val graph: Graph,
    val nodeSelection: NodeSelection,
)

typealias GraphHistory = List<Snapshot>

data class ContextCommand(
    val context: String,
    val command: String,
    val menu: String? = null
)

typealias KeystrokeBindings = Map<ContextCommand, String>
typealias CompressedKeystrokeBindings = Map<Int, List<ContextCommand>>

typealias GetShortcut = (String) -> String?

data class MenuChannel(
    val getShortcut: GetShortcut,
    val editor: Editor,
    val menus: ContextMenus,
)

enum class RenderingMode {
  flat,
  lit,
  wireframe,
}

data class ViewportState(
    val camera: CameraRig,
)

data class SceneState(
    val viewports: Map<Key, ViewportState> = defaultViewports(),
    val nodeSelection: NodeSelection = setOf(),
)

typealias SceneStates = Map<Key, SceneState>

object GizmoTypes {
  val collision = "collision"
}

data class EditorPersistentState(
    val graph: String? = null,
    val sceneStates: SceneStates = mapOf(),
    val renderingModes: Map<Key, RenderingMode> = mapOf(),
    val visibleGizmoTypes: Set<String> = setOf(),
    val fileSelection: Set<String> = setOf(),
    val expandedProjectTreeNodes: Set<String> = setOf(),
)

data class MenuItem(
    val label: String,
    val commandType: String? = null,
    val command: Command? = null,
    val getState: GetMenuItemState? = null,
    val weight: Int = 1000
)

data class MenuTree(
    val label: String,
    val commandType: String? = null,
    val command: Command? = null,
    val items: List<MenuTree>? = null,
    val key: String? = null,
    val getState: GetMenuItemState? = null,
)

typealias PathList = List<String>

typealias EditorDepiction = (Graph, Key) -> ElementGroup
typealias EditorDepictionMap = Map<Key, EditorDepiction>
typealias ContextMenus = Map<PathList, MenuItem>

data class GizmoEnvironment(
    val editor: Editor,
    val viewport: Vector4i,
    val camera: CameraRig,
    val transform: ScreenTransform,
    val drawList: ImDrawList,
)

typealias GizmoPainter = (GizmoEnvironment) -> Unit

typealias GraphEditor = (Editor, Command, Graph) -> Graph
typealias GraphEditors = Map<String, GraphEditor>

data class EditorEnumerations(
    val propertyDefinitions: PropertyDefinitions,
    val propertiesSerialization: PropertiesSerialization,
    val schema: PropertySchema = mapOf(),
    val attributes: List<Key> = listOf(),
    val meshes: List<Key> = listOf(),
    val resourceInfo: ResourceInfo,
    val collisionPresets: Map<Int, String> = mapOf(),
    val expanders: Expanders = mapOf(),
    val depictions: EditorDepictionMap = mapOf(),
    val menus: ContextMenus = mapOf(),
    val gizmoPainters: List<GizmoPainter>,
    val graphEditors: GraphEditors = mapOf(),
)

// Even if this only ever has one field, it's useful to wrap it to have a distinction between
// no response and a response where there was no matching object
data class SelectionQueryResponse(
    val selectedObject: Key? = null,
)

data class SelectionQuery(
    val position: Vector2i,
    val command: Command?,
    val response: SelectionQueryResponse? = null,
)

data class EditHistory(
    val pastAndPresent: GraphHistory = listOf(),
    val future: GraphHistory = listOf(),
)

typealias HistoryMap = Map<String, EditHistory>

enum class MouseAction {
  none,
  pan,
  orbit
}

data class Editor(
    val projectPath: Path,
    val persistentState: EditorPersistentState = EditorPersistentState(),
    val staging: Graph? = null,
    val clipboard: Graph? = null,
    val history: HistoryMap = mapOf(),
    val operation: Operation? = null,
    val enumerations: EditorEnumerations,
    val fileItems: FileItems,
    val graphLibrary: GraphLibrary = mapOf(),
    val viewportBoundsMap: Map<Key, Vector4i> = mapOf(),
    val bindings: KeystrokeBindings = defaultEditorMenuKeystrokes(),
    val selectionQuery: SelectionQuery? = null,
    val maxHistory: Int = 30,
    val flyThrough: Boolean = false,
    val mouseActionViewport: String? = null,
    val mouseAction: MouseAction = MouseAction.none,
    val selectedJoint: Key? = null,
    val previousActiveField: String? = null,
)

const val keypadKey = "Numpad"
const val numpadPeriodKey = "$keypadKey ."

typealias GetMenuItemState = (Editor) -> Boolean

enum class CollisionShape {
  box,
  composite,
  cylinder,
  mesh,
  sphere,
}

object Contexts {
  const val global = "global"
  const val nodes = "nodes"
  const val project = "project"
  const val viewport = "viewport"
  const val properties = "properties"
}

object Menus {
  const val camera = "camera"
  const val display = "display"
  const val edit = "edit"
  const val file = "file"
}
