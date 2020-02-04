package silentorb.metaview.common

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import silentorb.metahub.core.*
import silentorb.mythic.ent.*
import java.nio.file.Files
import java.nio.file.Paths

enum class FocusContext {
  graph,
  graphs,
  none
}

fun guiTransform(transform: (GuiState) -> GuiState): CommonTransform = { state ->
  state.copy(
      gui = transform(state.gui)
  )
}

fun graphTransform(transform: GraphTransform): CommonTransform = { state ->
  state.copy(
      graph = transformNotNull(state.graph, transform)
  )
}

fun activeGraphChanged(name: String?): CommonTransform = { state ->
  state.copy(
      gui = state.gui.copy(
          activeGraph = name,
          graphInteraction = GraphInteraction()
      )
  )
}

fun texturePath(state: CommonState, name: String): String =
    "${state.gui.graphDirectory}/$name.json"

fun loadTextureGraph(engine: Engine, state: CommonState, name: String): Graph =
    loadGraphFromFile(engine, texturePath(state, name))

fun fillInDefaults(nodeDefinitions: NodeDefinitionMap): GraphTransform = { graph ->
  val newValues = graph.functions.flatMap { f ->
    val definition = getDefinition(nodeDefinitions)(graph, f.key)
    definition.inputs.filter { input ->
      input.value.defaultValue != null && graph.values.none {
        it.node == f.key && it.port == input.key
      }
    }
        .map { (inputName, input) ->
          InputValue(
              node = f.key,
              port = inputName,
              value = input.defaultValue!!
          )
        }
  }
  graph.copy(
      values = graph.values.plus(newValues)
  )
}

fun selectGraphInternal(engine: Engine, nodeDefinitions: NodeDefinitionMap, name: String?): CommonTransform = { state: CommonState ->
  val graph = if (name != null)
    fillInDefaults(nodeDefinitions)(loadTextureGraph(engine, state, name))
  else
    null

  val gui = state.gui
  val interaction = gui.graphInteraction

  val nodeSelection = if (graph != null)
    interaction.nodeSelection.filter { graph.nodes.contains(it) }
  else
    listOf()

  val portSelection = if (graph != null)
    interaction.portSelection.filter { graph.nodes.contains(it.node) }
  else
    listOf()

  state.copy(
      graph = graph,
      gui = gui.copy(
          graphInteraction = interaction.copy(
              nodeSelection = nodeSelection,
              portSelection = portSelection
          )
      )
  )
}

fun selectGraph(engine: Engine, nodeDefinitions: NodeDefinitionMap, name: String?): CommonTransform = pipe(
    selectGraphInternal(engine, nodeDefinitions, name),
    activeGraphChanged(name)
)

fun refreshGraph(engine: Engine, nodeDefinitions: NodeDefinitionMap): CommonTransform = peek { state ->
  selectGraphInternal(engine, nodeDefinitions, state.gui.activeGraph)
}

fun isReselecting(id: Id, state: CommonState): Boolean =
    state.gui.graphInteraction.nodeSelection.contains(id)

fun outputNode(engine: Engine, graph: Graph): Id =
    graph.nodes.first { isOutputNode(engine.outputTypes)(graph, it) }

fun connectNodes(engine: Engine, id: Id): CommonTransform = { state ->
  val graph = state.graph!!
  if (isReselecting(id, state) || isOutputNode(engine.outputTypes)(graph, id))
    state
  else {
    val selectedPort = state.gui.graphInteraction.portSelection.first()
    val isNewPort = selectedPort.input == newPortString
    val port = if (isNewPort)
      Port(selectedPort.node, (graph.connections.count { it.output == selectedPort.node } + 1).toString())
    else
      selectedPort

    val additional = if (isNewPort)
      replaceValue<List<Float>>(port.node, "weights") { it.plus(0.5f) }
    else
      ::pass

    val newGraph = pipe(newConnection(id, port), additional)

    state.copy(
        gui = state.gui.copy(
            graphInteraction = state.gui.graphInteraction.copy(
                mode = GraphMode.normal
            )
        ),
        graph = newGraph(graph)
    )
  }
}

fun toggleNodeSelection(id: Id): CommonTransform = { state ->
  state.copy(
      gui = state.gui.copy(
          graphInteraction = if (isReselecting(id, state))
            state.gui.graphInteraction.copy(nodeSelection = state.gui.graphInteraction.nodeSelection.minus(id))
          else
            GraphInteraction(nodeSelection = listOf(id))
      )
  )
}

fun selectNode(engine: Engine, id: Id): CommonTransform = { state ->
  if (state.gui.graphInteraction.mode == GraphMode.connecting)
    connectNodes(engine, id)(state)
  else
    toggleNodeSelection(id)(state)
}

fun selectInput(port: Port): CommonTransform = { state ->
  val newSelection = if (state.gui.graphInteraction.portSelection.contains(port))
    state.gui.graphInteraction.copy(portSelection = state.gui.graphInteraction.portSelection.minus(port))
  else
    GraphInteraction(portSelection = listOf(port)).copy()

  state.copy(
      gui = state.gui.copy(
          graphInteraction = newSelection.copy(
              mode = GraphMode.normal
          )
      )
  )
}

fun changeInputValue(change: InputValue): CommonTransform = { state ->
  val graph = state.graph!!
  val newValues = replaceSingle(graph.values, change, ::isSameInput)
  state.copy(
      graph = graph.copy(
          values = newValues
      )
  )
}

fun renameGraph(change: Renaming): CommonTransform = { state ->
  Files.move(
      Paths.get(texturePath(state, change.previousName)),
      Paths.get(texturePath(state, change.newName))
  )
  state.copy(
      gui = state.gui.copy(
          activeGraph = change.newName
      ),
      graphNames = state.graphNames.map {
        if (it == change.previousName)
          change.newName
        else
          it
      }
  )
}

fun newGraph(name: String): CommonTransform = pipe({ state ->
  state.copy(
      graphNames = state.graphNames.plus(name).sorted(),
      graph = Graph()
  )
}, activeGraphChanged(name))

fun newNodeWithDefaults(nodeDefinitions: NodeDefinitionMap, name: String, id: Id): GraphTransform {
  val definition = nodeDefinitions[name]!!
  val values = definition.inputs.mapNotNull {
    val defaultValue = it.value.defaultValue
    if (defaultValue != null)
      Pair(it.key, defaultValue)
    else
      null
  }
      .associate { it }

  return newNode(name, values, id)
}

fun newNode(nodeDefinitions: NodeDefinitionMap, name: String): CommonTransform = { state ->
  val id = nextNodeId(state.graph!!)
  state.copy(
      graph = newNodeWithDefaults(nodeDefinitions, name, id)(state.graph),
      gui = state.gui.copy(
          graphInteraction = GraphInteraction(
              nodeSelection = listOf(id)
          )
      )
  )
}

fun insertNode(nodeDefinitions: NodeDefinitionMap, name: String): CommonTransform = pipe(
    { state ->
      val graph = state.graph!!
      val port = state.gui.graphInteraction.portSelection.first()
      val middleNode = nextNodeId(graph)
      val existingConnection = getConnection(graph, port)
      val additional = if (existingConnection != null) {
        val deletion = deleteConnections(listOf(Port(existingConnection.output, existingConnection.port)))
        val inputNode = existingConnection.input
        val inputDefinition = getDefinition(nodeDefinitions)(graph, inputNode)
        val outputDefinition = nodeDefinitions[name]!!
        val input = outputDefinition.inputs.entries
            .firstOrNull { it.value.type == inputDefinition.outputType }?.key
        if (input != null)
          pipe(
              newConnection(existingConnection.input, middleNode, input),
              deletion
          )
        else
          deletion
      } else
        ::pass

      // Additional needs to come first because it sometimes deletes the current connection
      // solely based on the output port, not which source node it is connected to,
      // which would match and delete the new connection.
      val changes = pipe(
          additional,
          newConnection(middleNode, port)

      )

      state.copy(
          graph = changes(graph)
      )
    },
    newNode(nodeDefinitions, name)
)

fun cleanupDynamicValuesForDeletedConnections(deletedPorts: List<Port>): GraphTransform = { graph ->
  val deletedDynamicPorts = deletedPorts.filter { it.input.toIntOrNull() != null }
  if (deletedDynamicPorts.size > 1)
    throw Error("Does not yet support deleting multiple dynamic connections at once. (Race conditions)")

  if (deletedDynamicPorts.none())
    graph
  else {
    val port = deletedDynamicPorts.first()
    val id = port.input.toInt()
    val higherPorts = graph.connections.filter {
      it.output == port.node && it.port.toIntOrNull() != null && it.port.toInt() > id
    }
        .map { Pair(it, it.port.toInt()) }

    val modifiedConnections = higherPorts.map { (connection, portId) ->
      renameInput(connection, (portId - 1).toString())
    }

    val g2 = pipe(
        replaceValue<List<Float>>(port.node, "weights") { it.filterIndexed { i, _ -> i != id - 1 } },
        pipe(modifiedConnections)
    )(graph)

    g2
  }
}

fun deleteGraphSelection(engine: Engine): CommonTransform = { state ->
  val selection = state.gui.graphInteraction
  val newGraph = transformNotNull(state.graph, pipe(
      deleteNodes(selection.nodeSelection.filter { !isOutputNode(engine.outputTypes)(state.graph!!, it) }),
      deleteConnections(selection.portSelection),
      cleanupDynamicValuesForDeletedConnections(selection.portSelection)
  ))

  state.copy(
      graph = newGraph,
      gui = state.gui.copy(
          graphInteraction = GraphInteraction()
      )
  )
}

fun confirmFileDeletion(name: String): Boolean {
  val dialog = Alert(Alert.AlertType.CONFIRMATION)
  val filename = "$name.json"
  dialog.title = "Deleting $filename"
  dialog.contentText = "Are you sure you want to delete $filename?"
  dialog.headerText = null
  dialog.graphic = null
  val result = dialog.showAndWait()
  return !result.isEmpty
      && result.get() == ButtonType.OK
}

val deleteGraph: CommonTransform = { state ->
  val name = state.gui.activeGraph
  if (name != null && confirmFileDeletion(name)) {
    Files.delete(Paths.get(texturePath(state, name)))
    val index = state.graphNames.indexOf(name)
    val newTextures = state.graphNames.minus(name)
    val newName = state.graphNames.getOrNull(index) ?: state.graphNames.firstOrNull()

    pipe(state.copy(
        graphNames = newTextures
    ), listOf(activeGraphChanged(newName)))
  } else
    state
}

fun deleteSelected(engine: Engine, focus: FocusContext): CommonTransform =
    when (focus) {
      FocusContext.graph -> deleteGraphSelection(engine)
      FocusContext.graphs -> deleteGraph
      else -> ::pass
    }

val startConnection: CommonTransform = { state ->
  if (state.gui.graphInteraction.portSelection.none())
    state
  else
    state.copy(
        gui = state.gui.copy(
            graphInteraction = state.gui.graphInteraction.copy(
                mode = GraphMode.connecting
            )
        )
    )
}

fun onConnecting(focus: FocusContext): CommonTransform =
    when (focus) {
      FocusContext.graph -> startConnection
      else -> ::pass
    }

fun setPreviewFinal(value: Boolean): CommonTransform = guiTransform { gui ->
  gui.copy(
      previewFinal = value
  )
}

fun copyNodeProperties(first: Id, second: Id): GraphTransform = { graph ->
  val newConnections = graph.connections.filter { it.output == first }
      .map { newConnection(it.input, second, it.port) }

  val newValues = graph.values.filter { it.node == first }
      .map { setValue(second, it.port, it.value) }

  pipe(newConnections.plus(newValues))(graph)
}

fun duplicateNode(nodeDefinitions: NodeDefinitionMap, node: Id): CommonTransform = { state ->
  val graph = state.graph!!
  val id = nextNodeId(graph)
  val name = graph.functions.getValue(node)
  val newState = newNode(nodeDefinitions, name)(state)
  graphTransform(copyNodeProperties(node, id))(newState)
}

val loadGraphs: CommonTransform = { state ->
  val graphs = listGraphs(state.gui.graphDirectory)
  val gui = state.gui
  state.copy(
      gui = gui.copy(
          activeGraph = gui.activeGraph ?: graphs.firstOrNull()
      ),
      graphNames = graphs
  )
}

fun commonStateListener(engine: Engine, nodeDefinitions: NodeDefinitionMap,
                        focus: () -> FocusContext):
    StateTransformListener<CommonState> = eventTypeSwitch { eventType: CommonEvent, data ->
  when (eventType) {
    CommonEvent.addNode -> newNode(nodeDefinitions, data as String)
    CommonEvent.deleteSelected -> deleteSelected(engine, focus())
    CommonEvent.duplicateNode -> duplicateNode(nodeDefinitions, data as Id)
    CommonEvent.connecting -> onConnecting(focus())
    CommonEvent.inputValueChanged -> changeInputValue(data as InputValue)
    CommonEvent.insertNode -> insertNode(nodeDefinitions, data as String)
    CommonEvent.graphSelect -> selectGraph(engine, nodeDefinitions, data as String)
    CommonEvent.newGraph -> newGraph(data as String)
    CommonEvent.selectInput -> selectInput(data as Port)
    CommonEvent.selectNode -> selectNode(engine, data as Id)
    CommonEvent.setPreviewFinal -> setPreviewFinal(data as Boolean)
    CommonEvent.refresh -> refreshGraph(engine, nodeDefinitions)
    CommonEvent.renameGraph -> renameGraph(data as Renaming)
  }
}
