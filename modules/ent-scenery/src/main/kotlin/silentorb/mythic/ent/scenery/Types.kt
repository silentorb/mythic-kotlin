package silentorb.mythic.ent.scenery

import silentorb.mythic.ent.GraphLibrary
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.Graph

data class ExpansionLibrary(
    val graphs: GraphLibrary,
    val expanders: Expanders = mapOf(),
)

typealias Expander = (ExpansionLibrary, Graph, Key) -> Graph?
typealias Expanders = Map<Key, Expander>
