package silentorb.metaview.common.views

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import silentorb.metahub.core.Engine
import silentorb.metahub.core.InputValue
import silentorb.metahub.core.getInputValue
import silentorb.metahub.core.isOutputNode
import silentorb.metaview.common.*

import silentorb.mythic.ent.Id

fun selectedNode(state: CommonState): Id? =
    state.gui.graphInteraction.nodeSelection.firstOrNull()
        ?: state.gui.graphInteraction.portSelection.firstOrNull()?.node

fun ifSelectedNode(engine: Engine, action: (CommonState, Id) -> Node): (CommonState) -> Node = { state ->
  val id = selectedNode(state)
  if (id == null || state.graph == null || isOutputNode(engine.outputTypes)(state.graph, id))
    VBox()
  else
    action(state, id)
}

fun propertiesView(nodeDefinitions: NodeDefinitionMap, engine: Engine, emit: Emitter) = ifSelectedNode(engine) { state, id ->
  val panel = VBox()
  panel.spacing = 5.0
  panel.alignment = Pos.BASELINE_CENTER
  val graph = state.graph!!
  val functionName = graph.functions[id]!!
  val label = Label(functionName)

  val grid = GridPane()
  grid.hgap = 10.0
  grid.vgap = 10.0
  grid.padding = Insets(10.0)

  panel.children.addAll(label, grid)

  val definition = nodeDefinitions[functionName]!!

  var index = 1
  definition.inputs.entries.forEach { (name, input) ->
    val viewFactory = valueViews[input.type]
    if (viewFactory != null) {
      val propertyLabel = Label(name)
      val value = getInputValue(graph)(id, name)!!
      val changed: OnChange = { newValue, preview ->
        //          if (newValue != value) {
        val data = InputValue(
            node = id,
            port = name,
            value = newValue
        )
        emit(Event(CommonEvent.inputValueChanged, data, preview))
//          }
      }

      val view = viewFactory(input)(value.value, changed)

      GridPane.setConstraints(propertyLabel, 1, index)
      GridPane.setConstraints(view, 2, index)
      grid.children.addAll(propertyLabel, view)
      index++
    }
  }
  panel
}
