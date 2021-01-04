package silentorb.mythic.editing

import silentorb.mythic.ent.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector4i
import java.nio.file.Path

const val sceneFileExtension = ".scene"

typealias SceneTree = Map<Key, Key>
typealias MenuDefinition = (GetShortcut) -> Commands
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

typealias SerializationMethod = (Any) -> Any

data class Serialization(
    val load: SerializationMethod,
    val save: SerializationMethod,
)

typealias DefaultValueSource = (Editor) -> Any?
typealias PropertyWidget = (Editor, Entry, String) -> Any

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
    val command: String
)

typealias KeystrokeBindings = Map<ContextCommand, String>
typealias CompressedKeystrokeBindings = Map<Int, List<ContextCommand>>

typealias GetShortcut = (String) -> String?

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

// Persistent State
data class EditorState(
    val graph: String? = null,
    val sceneStates: SceneStates = mapOf(),
    val renderingModes: Map<Key, RenderingMode> = mapOf(),
    val fileSelection: Set<String> = setOf(),
)

data class EditorEnumerations(
    val propertyDefinitions: PropertyDefinitions,
    val schema: PropertySchema = mapOf(),
    val attributes: List<Key> = listOf(),
    val textures: List<Key> = listOf(),
    val meshes: List<Key> = listOf(),
    val collisionPresets: Map<Int, String> = mapOf(),
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
    val state: EditorState = EditorState(),
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
)

const val keypadKey = "Numpad"
const val numpadPeriodKey = "$keypadKey ."

data class MenuItem(
    val label: String,
    val command: String? = null,
    val items: List<MenuItem>? = null
)

enum class CollisionShape {
  box,
  composite,
  cylinder,
  mesh,
}

object Contexts {
  const val global = "global"
  const val nodes = "nodes"
  const val project = "project"
  const val viewport = "viewport"
  const val properties = "properties"
}
