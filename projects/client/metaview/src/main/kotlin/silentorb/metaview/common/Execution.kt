package silentorb.metaview.common

import silentorb.metahub.core.*

typealias ValueMap = Map<String, () -> Any>

fun sanitizeGraph(nodeDefinitions: NodeDefinitionMap, defaultValues: ValueMap): (Graph) -> Graph = { graph ->
  val changes = graph.nodes.flatMap { node ->
    val definition = getDefinition(nodeDefinitions)(graph, node)
    definition.inputs.mapNotNull { input ->
      val connection = graph.connections
          .filter { it.output == node && it.port == input.key }
          .firstOrNull()

      if (connection == null && graph.values.none { it.node == node && it.port == input.key }) {
        Pair(node, input)
      } else
        null
    }
  }

  val newValues = changes.map { (node, input) ->
    val getValue = defaultValues[input.value.type]
    if (getValue == null)
      throw Error("Type ${input.key} cannot be null")
    InputValue(
        value = getValue(),
        node = node,
        port = input.key
    )
  }

  graph.copy(
      values = graph.values.plus(newValues)
  )
}

fun executeSanitized(nodeDefinitions: NodeDefinitionMap, defaultValues: ValueMap, engine: Engine, graph: Graph): OutputValues {
  val tempGraph = sanitizeGraph(nodeDefinitions, defaultValues)(graph)
  return execute(engine, tempGraph)
}