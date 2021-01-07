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
        .minus(editor.persistentState.graph ?: "")
        .toList()

fun drawFormField(editor: Editor, definition: PropertyDefinition, entry: Entry, id: String): Any {
  val widget = definition.widget
  return if (widget != null) {
    widget(editor, entry, id)
  } else
    entry.target
}

private var previousNode: String? = null
private var propertyOrderState: MutableMap<String, LooseGraph> = mutableMapOf()

fun managePropertyOrder(entries: LooseGraph): LooseGraph =
    if (entries.size < 2)
      entries
    else {
      val first = entries.first()
      if (first.source != previousNode) {
        propertyOrderState.clear()
        previousNode = first.source
      }

      val key = first.property
      val previous = propertyOrderState[key]
      if (previous == null) {
        propertyOrderState[key] = entries
        entries
      } else {
        entries.sortedBy {
          val index = previous.indexOf(it)
          if (index == -1)
            1000
          else
            index
        }
      }
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
    val libraryAttributes = editor.graphLibrary.keys
    val expanders = editor.enumerations.expanders.keys
    val allTypes = (
        allAttributes +
            getAvailableTypes(editor) +
            libraryAttributes +
            expanders
        )
        .distinct()

    val availableAttributes = allTypes - attributes // - node

    for (attribute in availableAttributes.sorted()) {
      if (ImGui.selectable(attribute)) {
        commands = commands.plus(Command(EditorCommands.setGraphValue, value = Entry(node, SceneProperties.type, attribute)))
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
        val selection = getNodeSelection(editor)
        if (selection.size == 1) {
          val node = selection.first()
          val definitions = editor.enumerations.propertyDefinitions
          val entries = getProperties(graph, node)

          ImGui.text(node)
          ImGui.separator()
          val existing = entries.map { it.property } - editor.enumerations.schema.filter { it.value.manyToMany }.keys
          val availableDefinitions = definitions.minus(existing)
          val union = getPropertyValues<Key>(graph, node, SceneProperties.type)
          if (availableDefinitions.any()) {
            commands = commands + addPropertiesDropDown(editor, availableDefinitions, union, entries, node)
          }

          ImGui.separator()
          for (attribute in union) {
            ImGui.text(attribute)
            ImGui.sameLine()
            if (ImGui.smallButton("x##attribute-$attribute")) {
              commands = commands.plus(Command(EditorCommands.removeGraphValue, value = Entry(node, SceneProperties.type, attribute)))
            }
          }

          for ((property, definition) in definitions) {
            val propertyEntries = managePropertyOrder(entries.filter { it.property == property })
            propertyEntries.forEachIndexed { index, entry ->
              ImGui.separator()
              val nextValue = if (definition.widget != null) {
                ImGui.text(definition.displayName)
                ImGui.sameLine()
                val id = "${entry.source}.${entry.property}.${index}"

                if (ImGui.smallButton("x##property-${id}")) {
                  commands = commands.plus(Command(EditorCommands.removeGraphValue, value = entry))
                }
                drawFormField(editor, definition, entry, id)
              } else
                entry.target

              if (nextValue != entry.target) {
                val command = if (isManyToMany(editor, property))
                  Command(EditorCommands.replaceGraphValue, value = entry to Entry(node, property, nextValue))
                else
                  Command(EditorCommands.setGraphValue, value = Entry(node, property, nextValue))

                commands = commands + command
              }
            }
          }
        }
        commands
      } else
        listOf<Command>()
    }
