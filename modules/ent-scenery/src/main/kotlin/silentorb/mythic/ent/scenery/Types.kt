package silentorb.mythic.ent.scenery

import silentorb.mythic.ent.GraphLibrary
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.Graph
import silentorb.mythic.ent.PropertySchema

data class ExpansionLibrary(
    val graphs: GraphLibrary,
    val schema: PropertySchema,
)

typealias Expander = (ExpansionLibrary, Graph, Key) -> Graph?
typealias Expanders = Map<Key, Expander>
