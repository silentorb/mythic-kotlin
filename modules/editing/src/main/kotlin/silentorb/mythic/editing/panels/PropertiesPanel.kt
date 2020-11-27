package silentorb.mythic.editing.panels

import imgui.ImGui
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.panel
import silentorb.mythic.editing.components.panelBackground
import silentorb.mythic.ent.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.scenery.SceneProperties

fun getAvailableTypes(editor: Editor): List<Key> =
    getSceneFiles(editor)
        .map { sceneFileNameWithoutExtension(it.name) }
        .minus(editor.state.graph ?: "")
        .toList()

fun drawFormField(editor: Editor, definition: PropertyDefinition, entry: Entry): Any {
  val widget = definition.widget
  return if (widget != null) {
    widget(editor, entry)
  } else
    entry.target
}

fun addPropertiesDropDown(editor: Editor, availableDefinitions: PropertyDefinitions, attributes: List<Key>, entries: Graph, node: Key): Commands {
  var commands: Commands = listOf()
  if (ImGui.beginCombo("Add Property", "")) {
    for ((property, definition) in availableDefinitions.entries.sortedBy { it.value.displayName }) {
      if (ImGui.selectable(definition.displayName)) {
        val target = definition.defaultValue?.invoke(editor) ?: ""
        val missingDependencies = definition.dependencies - entries.map { it.property }
        val addPropertyCommands = listOf(
            Command(EditorCommands.setGraphValue, value = Entry(node, property, target))
        )
            .plus(
                missingDependencies.mapNotNull {
                  val dependencyDefinition = editor.enumerations.propertyDefinitions[it]
                  if (dependencyDefinition != null) {
                    val dependencyValue = dependencyDefinition.defaultValue?.invoke(editor) ?: ""
                    Command(EditorCommands.setGraphValue, value = Entry(node, it, dependencyValue))
                  } else
                    null
                }
            )
        commands = commands.plus(addPropertyCommands)
      }
    }
    if (commands.none()) {
      activeInputType = InputType.dropdown
    }
    ImGui.endCombo()
  }

  if (ImGui.beginCombo("Add Type", "")) {
    val allAttributes = editor.enumerations.attributes
    val allTypes = getAvailableTypes(editor)
    val availableAttributes = allAttributes + allTypes - attributes - node

    for (attribute in availableAttributes.sorted()) {
      if (ImGui.selectable(attribute)) {
        commands = commands.plus(Command(EditorCommands.setGraphValue, value = Entry(node, SceneProperties.instance, attribute)))
      }
    }
    if (commands.none()) {
      activeInputType = InputType.dropdown
    }
    ImGui.endCombo()
  }

  return commands
}

fun drawPropertiesPanel(editor: Editor, graph: Graph?): PanelResponse =
    panel(editor, "Properties", Contexts.properties, null) {
      panelBackground()

      if (graph != null) {
        var commands: Commands = listOf()
        val selection = editor.state.nodeSelection
        if (selection.size == 1) {
          val node = selection.first()
          val definitions = editor.enumerations.propertyDefinitions
          val entries = getProperties(graph, node)

          ImGui.text(node)
          ImGui.separator()
          val availableDefinitions = definitions.minus(entries.map { it.property })
          val union = getPropertyValues<Key>(graph, node, SceneProperties.instance)
          if (availableDefinitions.any()) {
            commands = commands + addPropertiesDropDown(editor, availableDefinitions, union, entries, node)
          }

          ImGui.separator()
          for (attribute in union) {
            ImGui.text(attribute)
            ImGui.sameLine()
            if (ImGui.smallButton("x##attribute-$attribute")) {
              commands = commands.plus(Command(EditorCommands.removeGraphValue, value = Entry(node, SceneProperties.instance, attribute)))
            }
          }

          for ((property, definition) in definitions) {
            val entry = entries.firstOrNull { it.property == property }
            if (entry != null) {
              ImGui.separator()
              val nextValue = if (definition.widget != null) {
                ImGui.text(definition.displayName)
                ImGui.sameLine()
                if (ImGui.smallButton("x##property-${entry.target}")) {
                  commands = commands.plus(Command(EditorCommands.removeGraphValue, value = entry))
                }
                drawFormField(editor, definition, entry)
              } else
                entry.target

              if (nextValue != entry.target) {
                commands = commands.plus(Command(EditorCommands.setGraphValue, value = Entry(node, property, nextValue)))
              }
            }
          }
        }
        commands
      } else
        listOf<Command>()
    }
