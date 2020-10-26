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

data class Editor(
    val isActive: Boolean = false,
    val graphLibrary: GraphLibrary = mapOf(),
    val graph: String? = null,
    val selection: NodeSelection = setOf(),
    val viewport: Vector4i? = null,
    val propertyDefinitions: PropertyDefinitions
)
