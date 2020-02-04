package silentorb.metaview.common.views

import javafx.application.Platform
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import silentorb.metaview.common.*

import silentorb.mythic.ent.Id
import org.joml.Vector2i
import silentorb.metahub.core.Engine
import silentorb.metahub.core.Graph
import silentorb.metahub.core.Port
import silentorb.metahub.core.arrangeGraphStages

const val nodeLength: Int = 75
const val nodePadding: Int = 40
const val portPadding: Int = 110

fun portLabel(port: Port, emit: Emitter, selection: List<Port>): Node {
  val label = Label(port.input)
  label.setOnMouseClicked { emit(Event(CommonEvent.selectInput, port)) }
  if (selection.contains(port)) {
    val borderStroke = BorderStroke(Color.BLUEVIOLET, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
    label.border = Border(borderStroke)
  }
  return label
}

fun getInputs(nodeDefinitions: NodeDefinitionMap, connectableTypes: Set<String>, graph: Graph, node: Id): Map<String, InputDefinition> {
  val definition = getDefinition(nodeDefinitions)(graph, node)
  val additionalInputs = if (definition.variableInputs != null) {
    getDynamicPorts(graph, node, definition.variableInputs)
        .plus(Pair(newPortString, InputDefinition(type = definition.variableInputs)))
  } else
    mapOf()
  return definition.inputs
      .filterValues { connectableTypes.contains(it.type) }
      .plus(additionalInputs)
}

fun portLabels(nodeDefinitions: NodeDefinitionMap, connectableTypes: Set<String>, graph: Graph, emit: Emitter, selection: List<Port>, node: Id): List<Node> {
  return getInputs(nodeDefinitions, connectableTypes, graph, node)
      .map { input ->
        val port = Port(node = node, input = input.key)
        portLabel(port, emit, selection)
      }
}

fun getBoundsRelativeToParent(parent: Node, child: Node): Point2D {
  val position = child.localToScene(0.0, 0.0)
  return position.subtract(parent.localToScene(0.0, 0.0))
}

fun drawConnection(gc: GraphicsContext, pane: Node, nodeNode: Node, port: Node) {
  val a = getBoundsRelativeToParent(pane, nodeNode)
  val b = getBoundsRelativeToParent(pane, port)
  gc.stroke = Color.GREEN
  gc.lineWidth = 2.0
  gc.strokeLine(
      a.x + nodeNode.boundsInParent.width + 7,
      a.y + nodeNode.boundsInParent.height / 2,
      b.x - 7,
      b.y + 10.0
  )
}

private const val strideX = nodeLength + nodePadding + portPadding
private const val strideY = nodeLength + nodePadding

fun nodePosition(x: Int, y: Int): Vector2i {

  return Vector2i(
      nodePadding + x * strideX, nodePadding + y * strideY
  )
}

fun graphCanvas(nodeDefinitions: NodeDefinitionMap, connectableTypes: Set<String>, graph: Graph, stages: List<List<Id>>, pane: Pane, nodeNodes: Map<Id, Pair<Node, List<Node>>>): Node {
  val tempPosition = nodePosition(stages.size + 2, stages.map { it.size }.sortedDescending().first() + 1)
  val canvas = Canvas(tempPosition.x.toDouble(), tempPosition.y.toDouble())

  val gc = canvas.graphicsContext2D
//    gc.setFill(Color.BLUE);
//    gc.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight())

  Platform.runLater {
    graph.connections.forEach { connection ->
      val input = nodeNodes[connection.input]!!
      val output = nodeNodes[connection.output]!!
      val inputs = getInputs(nodeDefinitions, connectableTypes, graph, connection.output)
      val port = output.second[inputs.keys.indexOf(connection.port)]
      drawConnection(gc, pane, input.first, port)
    }
  }

  return canvas
}

fun graphView(engine: Engine, nodeDefinitions: NodeDefinitionMap, connectableTypes: Set<String>, valueDisplays: ValueDisplayMap, emit: Emitter, state: CommonState): Node {
  val pane = Pane()
  val graph = state.graph
  if (graph != null) {
    val nodeNodes = mutableMapOf<Id, Pair<Node, List<Node>>>()

    val stages = arrangeGraphStages(engine.outputTypes, graph)
        .plusElement(graph.nodes.filter { engine.outputTypes.contains(graph.functions[it]) })

    stages.forEachIndexed { x, stage ->
      stage.forEachIndexed { y, nodeId ->
        val nodeValue = state.outputValues[nodeId]
        val hbox = HBox()
         val position = nodePosition(x, y)
        hbox.relocate(position.x.toDouble(), position.y.toDouble())
        val portsPanel = VBox()
        portsPanel.spacing = 5.0
        portsPanel.prefWidth = 70.0
        val portLabels = portLabels(nodeDefinitions, connectableTypes, graph, emit, state.gui.graphInteraction.portSelection, nodeId)
        portsPanel.children.addAll(portLabels)
        nodeNodes[nodeId] = Pair(hbox, portLabels)
        hbox.children.add(portsPanel)
        if (nodeValue != null) {
          val icon = nodeIcon(valueDisplays, nodeDefinitions, emit, graph, nodeId, nodeValue)
          if (state.gui.graphInteraction.nodeSelection.contains(nodeId)) {
            val borderStroke = BorderStroke(Color.BLUEVIOLET, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
            icon.border = Border(borderStroke)
          }
          hbox.children.add(icon)
        }

        pane.children.add(hbox)
      }
    }

    val canvas = graphCanvas(nodeDefinitions, connectableTypes, graph, stages, pane, nodeNodes)
    pane.children.add(0, canvas)
  }

  return pane
}

fun graphViewListener(engine: Engine, nodeDefinitions: NodeDefinitionMap,
                      connectableTypes: Set<String>, valueDisplays: ValueDisplayMap,
                      emit: Emitter, setter: (Node) -> Unit): SideEffectStateListener<CommonState> = { change ->
  val next = change.next
  val previous = change.previous
  if (next.graph != previous.graph || next.gui != previous.gui || change.event.type == CommonEvent.refresh) {
    val view = graphView(engine, nodeDefinitions, connectableTypes, valueDisplays, emit, change.next)
    setter(view)
  }
}
