package silentorb.mythic.editing

import silentorb.mythic.spatial.Vector4i

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

data class PropertyDefinition(
    val displayName: String,
    val serialization: Serialization? = null,
    val widget: Id?,
    val dependencies: Set<Id> = setOf()
)

typealias PropertyDefinitions = Map<Id, PropertyDefinition>

typealias NodeSelection = Set<Id>

data class Option(
    val label: String,
    val value: String,
)

typealias Options = List<Option>

data class EditorState(
    val graph: String? = null,
    val cameras: Map<Id, CameraRig> = mapOf(),
    val viewportBoundsMap: Map<Id, Vector4i> = mapOf(),
    val selection: NodeSelection = setOf(),
    val graphLibrary: GraphLibrary = mapOf(),
)

data class Editor(
    val state: EditorState = EditorState(),
    val propertyDefinitions: PropertyDefinitions,
    val textures: Options = listOf(),
    val meshes: Options = listOf(),
)
