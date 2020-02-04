package silentorb.metaview.common.views

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import silentorb.imp.core.Graph
import silentorb.metaview.common.Emitter
import silentorb.metaview.common.Event
import silentorb.metaview.common.CommonEvent
import silentorb.metaview.common.NodeDefinitionMap

import silentorb.mythic.ent.Id

typealias ValueDisplay = (Any) -> Image
typealias ValueDisplayMap = Map<String, ValueDisplay>

fun getNodePreviewBuffer(valueDisplays: ValueDisplayMap, type: String, value: Any): Image? {
  val display = valueDisplays[type]
  return if (display != null)
    display(value)
  else
    null

//  return when (type) {
//    bitmapType -> rgbFloatToBytes(value as FloatBuffer)
//    grayscaleType -> grayscaleTextureToBytes(value as FloatBuffer)
//    else -> throw Error("Not supported")
//  }
}

fun emptyNode(): Pane {
  val box = Pane()
  box.prefWidth = nodeLength.toDouble()
  box.prefHeight = nodeLength.toDouble()
  return box
}

fun getNodePreviewBuffer(valueDisplays: ValueDisplayMap, nodeDefinitions: NodeDefinitionMap, graph: Graph, node: Id, value: Any): Image? {
  val function = graph.functions[node]!!
  val definition = nodeDefinitions[function]!!
  return getNodePreviewBuffer(valueDisplays, definition.outputType, value)
}

fun nodeIcon(valueDisplays: ValueDisplayMap, nodeDefinitions: NodeDefinitionMap, emit: Emitter, graph: Graph, id: Id, value: Any?): Region {
  val imageView = if (value != null) {
    val image = getNodePreviewBuffer(valueDisplays, nodeDefinitions, graph, id, value)
    if (image != null) {
      val v = ImageView(image)
      v.fitWidth = nodeLength.toDouble()
      v.fitHeight = nodeLength.toDouble()
      v
    } else
      emptyNode()
  } else
    emptyNode()

  val container = VBox()
  val name = graph.functions[id] ?: "Unknown"
  val label = Label(name)
  container.alignment = Pos.BASELINE_CENTER
  container.children.addAll(imageView, label)

  container.setOnMouseClicked {
    emit(Event(CommonEvent.selectNode, id))
  }

  return container
}

