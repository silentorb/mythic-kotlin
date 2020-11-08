package silentorb.mythic.editing

import silentorb.mythic.spatial.Vector4i
import java.nio.file.Path

const val sceneFileExtension = ".scene"

typealias SceneTree = Map<Id, Id>

typealias GraphLibrary = Map<String, Graph>

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

data class PropertyDefinition(
    val displayName: String,
    val serialization: Serialization? = null,
    val widget: Id?,
    val dependencies: Set<Id> = setOf(),
    val defaultValue: DefaultValueSource? = null
)

typealias PropertyDefinitions = Map<Id, PropertyDefinition>

typealias NodeSelection = Set<Id>

data class Option(
    val label: String,
    val value: String,
)

typealias Options = List<Option>
typealias GraphHistory = List<Graph>

// Persistent State
data class EditorState(
    val graph: String? = null,
    val cameras: Map<Id, CameraRig> = mapOf(),
    val nodeSelection: NodeSelection = setOf(),
    val fileSelection: Set<String> = setOf(),
)

data class Editor(
    val projectPath: Path,
    val state: EditorState = EditorState(),
    val staging: Graph? = null,
    val graph: Graph? = null,
//    val history: GraphHistory = listOf(),
    val operation: Operation? = null,
    val propertyDefinitions: PropertyDefinitions,
    val textures: List<Id> = listOf(),
    val meshes: List<Id> = listOf(),
    val fileItems: FileItems,
    val graphLibrary: GraphLibrary = mapOf(),
    val viewportBoundsMap: Map<Id, Vector4i> = mapOf(),
)

const val keypadKey = "Numpad"
const val numpadPeriodKey = "$keypadKey ."

data class MenuItem(
    val label: String,
    val shortcut: String? = null,
    val command: Any? = null,
    val items: List<MenuItem>? = null
)
