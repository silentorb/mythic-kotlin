package silentorb.mythic.ent.scenery

import silentorb.mythic.ent.GraphLibrary
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.PropertySchema
import silentorb.mythic.scenery.Shape

data class ExpansionLibrary(
    val graphs: GraphLibrary,
    val expanders: Expanders,
    val schema: PropertySchema,
    val meshShapes: Map<String, Shape>,
)

typealias Expander = (ExpansionLibrary, Graph, Key) -> Graph?
typealias Expanders = Map<Key, Expander>
