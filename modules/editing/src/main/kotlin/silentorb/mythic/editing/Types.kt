package silentorb.mythic.editing

import silentorb.mythic.ent.Entry
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.GraphLibrary
import silentorb.mythic.ent.Key
import silentorb.mythic.spatial.Vector4i
import java.nio.file.Path

const val sceneFileExtension = ".scene"

typealias SceneTree = Map<Key, Key>

data class Typeface(
    val name: String,
    val path: String,
    val size: Float
)

typealias SerializationMethod = (Any) -> Any

data class Serialization(
    val load: SerializationMethod,
    val save: SerializationMethod,
)

typealias DefaultValueSource = (Editor) -> Any?
typealias OptionsSource = (Editor) -> List<Key>
typealias PropertyWidget = (Editor, Entry) -> Any

data class PropertyDefinition(
    val displayName: String,
    val serialization: Serialization? = null,
    val widget: PropertyWidget?,
    val dependencies: Set<Key> = setOf(),
    val defaultValue: DefaultValueSource? = null,
    val single: Boolean = true,
)

typealias PropertyDefinitions = Map<Key, PropertyDefinition>

typealias NodeSelection = Set<Key>

data class Option(
    val label: String,
    val value: String,
)

typealias Options = List<Option>
typealias GraphHistory = List<Graph>

// Persistent State
data class EditorState(
    val graph: String? = null,
    val cameras: Map<Key, CameraRig> = mapOf(),
    val nodeSelection: NodeSelection = setOf(),
    val fileSelection: Set<String> = setOf(),
)

data class EditorEnumerations(
    val propertyDefinitions: PropertyDefinitions,
    val attributes: List<Key> = listOf(),
    val textures: List<Key> = listOf(),
    val meshes: List<Key> = listOf(),
    val collisionGroups: List<Key> = listOf(),
)

data class Editor(
    val projectPath: Path,
    val state: EditorState = EditorState(),
    val staging: Graph? = null,
    val graph: Graph? = null,
//    val history: GraphHistory = listOf(),
    val operation: Operation? = null,
    val enumerations: EditorEnumerations,
    val fileItems: FileItems,
    val graphLibrary: GraphLibrary = mapOf(),
    val viewportBoundsMap: Map<Key, Vector4i> = mapOf(),
)

const val keypadKey = "Numpad"
const val numpadPeriodKey = "$keypadKey ."

data class MenuItem(
    val label: String,
    val shortcut: String? = null,
    val command: Any? = null,
    val items: List<MenuItem>? = null
)

enum class CollisionShape {
  box,
  composite,
  cylinder,
  mesh,
}
