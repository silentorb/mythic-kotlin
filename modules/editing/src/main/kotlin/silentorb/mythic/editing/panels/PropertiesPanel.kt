package silentorb.mythic.editing.panels

import imgui.ImGui
import imgui.flag.ImGuiWindowFlags
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.dropDownWidget
import silentorb.mythic.editing.components.panelBackground
import silentorb.mythic.editing.components.spatialWidget
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands

fun drawFormField(editor: Editor, definition: PropertyDefinition, entry: Entry): Any {
  ImGui.text(definition.displayName)
  return when (definition.widget!!) {
    Widgets.textureSelect -> dropDownWidget(editor.textures, entry)
    Widgets.meshSelect -> dropDownWidget(editor.meshes, entry)
    Widgets.translation -> spatialWidget(entry)
    Widgets.rotation -> spatialWidget(entry)
    Widgets.scale -> spatialWidget(entry)
    else -> entry.target
  }
}

fun drawPropertiesPanel(editor: Editor, graph: Graph?): Pair<Graph?, Commands> {
  ImGui.begin("Properties", ImGuiWindowFlags.MenuBar)
  panelBackground()

  val result = if (graph != null) {
    var nextGraph: Graph = graph
    val selection = editor.state.selection
    if (selection.size == 1) {
      val node = selection.first()
      val definitions = editor.propertyDefinitions
      val entries = getProperties(graph, node)

      ImGui.text(node)
      ImGui.separator()
      val availableDefinitions = definitions.minus(entries.map { it.property })
      if (availableDefinitions.any()) {
        if (ImGui.beginCombo("Add Property", "")) {
          for ((property, definition) in availableDefinitions) {
            if (ImGui.selectable(definition.displayName)) {
              val target = definition.defaultValue?.invoke(editor) ?: ""
              nextGraph = nextGraph + Entry(node, property, target)
            }
          }
          ImGui.endCombo()
        }
      }

      ImGui.separator()
      for ((property, definition) in definitions) {
        val entry = entries.firstOrNull { it.property == property }
        if (entry != null) {
//          val definition = definitions[entry.property]
          ImGui.separator()
          val nextValue = if (definition.widget != null)
            drawFormField(editor, definition, entry)
          else
            entry.target

          if (nextValue != entry.target) {
            nextGraph = nextGraph
                .minus(entry)
                .plus(entry.copy(target = nextValue))
          }
        }
      }
    }
    nextGraph to listOf<Command>()
  } else
    graph to listOf<Command>()

  ImGui.end()

  return result
}
