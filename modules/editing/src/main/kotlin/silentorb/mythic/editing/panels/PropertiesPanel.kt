package silentorb.mythic.editing.panels

import imgui.ImColor
import imgui.ImGui
import imgui.ImVec2
import silentorb.mythic.editing.components.panel
import silentorb.mythic.editing.components.panelBackground
import silentorb.mythic.editing.general.InputType
import silentorb.mythic.editing.general.MenuTree
import silentorb.mythic.editing.general.activeInputType
import silentorb.mythic.editing.main.*
import silentorb.mythic.ent.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.scenery.SceneProperties

fun propertiesMenus(): List<MenuTree> =
    listOf(
        MenuTree("Edit", key = Menus.edit, items = listOf(
            MenuTree("Copy Properties", EditorCommands.copyProperties),
        ))
    )

fun getAvailableTypes(editor: Editor): List<Key> =
    getSceneFiles(editor)
        .map { sceneFileNameWithoutExtension(it.name) }
        .minus(editor.persistentState.graph ?: "")
        .toList()

fun drawFormField(editor: Editor, definition: PropertyDefinition, entry: Entry, id: String): Any? {
  val widget = definition.widget
  return if (widget != null) {
    widget(editor, entry, id)
  } else
    entry.target
}

private var previousNode: String? = null
private var propertyOrderState: MutableMap<String, Graph> = mutableMapOf()

fun managePropertyOrder(entries: Graph): Graph =
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

fun addPropertiesDropDown(editor: Editor, availableDefinitions: PropertyDefinitions, attributes: List<Key>,
                          entries: Graph, nodes: Collection<Key>): Commands {
  var commands: Commands = listOf()
  if (ImGui.beginCombo("Add Property", "")) {
    for ((property, definition) in availableDefinitions.entries.sortedBy { it.value.displayName }) {
      if (ImGui.selectable(definition.displayName)) {
        val target = definition.defaultValue?.invoke(editor) ?: ""
        val missingDependencies = definition.dependencies - entries.map { it.property }
        val addPropertyCommands = nodes.map {
          Command(EditorCommands.setGraphValue, value = Entry(it, property, target))
        }
            .plus(
                missingDependencies
                    .mapNotNull { dependency ->
                      val dependencyDefinition = editor.enumerations.propertyDefinitions[dependency]
                      if (dependencyDefinition != null) {
                        val dependencyValue = dependencyDefinition.defaultValue?.invoke(editor) ?: ""
                        nodes.map {
                          Command(EditorCommands.setGraphValue, value = Entry(it, dependency, dependencyValue))
                        }
                      } else
                        listOf()
                    }
                    .flatten()
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
        commands = commands + nodes.map {
          Command(EditorCommands.setGraphValue, value = Entry(it, SceneProperties.type, attribute))
        }
      }
    }
    if (commands.none()) {
      activeInputType = InputType.dropdown
    }
    ImGui.endCombo()
  }

  return commands
}

data class MixedValue(
    val unused: Boolean = true
)

val mixedValue = MixedValue()

fun drawSelectableLabel(editor: Editor, key: Key, text: String): Commands {
  val isSelected = editor.persistentState.propertySelection.contains(key)
  if (isSelected) {
    val bounds = ImVec2()
    ImGui.calcTextSize(bounds, text)
    val drawList = ImGui.getWindowDrawList()
    val backgroundColor = ImColor.intToColor(150, 128, 255, 255)
    val cursorX = ImGui.getCursorScreenPosX()
    val cursorY = ImGui.getCursorScreenPosY()
    drawList.addRectFilled(cursorX, cursorY, cursorX + bounds.x, cursorY + bounds.y, backgroundColor)
  }
  ImGui.text(text)
  return if (ImGui.isItemClicked())
    listOf(Command(EditorCommands.selectProperty, value = key))
  else
    listOf()
}

fun drawPropertyField(editor: Editor, definition: PropertyDefinition, entry: Entry, id: String, singleNode: Key, property: Key): Commands {
  var commands: Commands = listOf()
  ImGui.separator()
  val nextValue = if (definition.widget != null) {
    val text = definition.displayName
    commands = commands + drawSelectableLabel(editor, property, text)
    ImGui.sameLine()

    if (ImGui.smallButton("x##property-${id}")) {
      commands = commands + Command(EditorCommands.removeGraphValue, value = entry)
    }
    drawFormField(editor, definition, entry, id)
  } else
    entry.target

  if (nextValue != null && nextValue != entry.target) {
    val command = if (isManyToMany(editor, property))
      Command(EditorCommands.replaceGraphValue, value = entry to Entry(singleNode, property, nextValue))
    else
      Command(EditorCommands.setGraphValue, value = Entry(singleNode, property, nextValue))

    commands = commands + command
  }
  return commands
}

fun drawPropertiesPanel(editor: Editor, graph: Graph?): PanelResponse =
    panel(editor, "Properties", Contexts.properties) {
      panelBackground()

      if (graph != null) {
        var commands: Commands = listOf()
        val nodes = getNodeSelection(editor)
        if (nodes.any()) {
          val singleNode = nodes.first()
          val definitions = editor.enumerations.propertyDefinitions
          val schema = editor.enumerations.schema
          val entries = nodes.flatMap { getProperties(graph, it) }

          if (nodes.size == 1) {
            ImGui.text(singleNode)
            ImGui.separator()
          }

          val existing = entries.map { it.property } - schema.filter { it.value.manyToMany }.keys
          val availableDefinitions = definitions.minus(existing)
          val union = filterByProperty(entries, SceneProperties.type)
              .map { it.target as String }
              .distinct()

          if (availableDefinitions.any()) {
            commands = commands + addPropertiesDropDown(editor, availableDefinitions, union, entries, nodes)
          }

          ImGui.separator()
          for (attribute in union) {
            commands = commands + drawSelectableLabel(editor, attribute, attribute)
            ImGui.sameLine()
            if (ImGui.smallButton("x##attribute-$attribute")) {
              commands = commands + nodes.map {
                Command(EditorCommands.removeGraphValue, value = Entry(it, SceneProperties.type, attribute))
              }
            }
          }

          if (nodes.size == 1) {
            for ((property, definition) in definitions) {
              val propertyEntries = managePropertyOrder(entries.filter { it.property == property })
              propertyEntries.forEachIndexed { index, entry ->
                val id = "${entry.source}.${entry.property}.${index}"
                commands = commands + drawPropertyField(editor, definition, entry, id, singleNode, property)
              }
            }
          } else {
            val propertyKeys = entries
                .map { it.property }
                .distinct()
                .filter { !(schema[it]?.manyToMany ?: false) }

            val compositeId = nodes.hashCode().toString()
            propertyKeys.forEachIndexed { index, property ->
              val definition = definitions[property]
              if (definition != null) {
                ImGui.separator()
                val previous = entries.filter { it.property == property }
                val nextValue = if (definition.widget != null) {
                  ImGui.text(definition.displayName)
                  ImGui.sameLine()
                  val id = "${compositeId}.${property}.${index}"

                  if (ImGui.smallButton("x##property-${id}")) {
                    commands = commands + nodes.map { Command(EditorCommands.removeGraphValue, value = it) }
                  }
                  val previousValue = if (previous.map { it.target }.distinct().count() == 1)
                    previous.first().target
                  else
                    mixedValue

                  val entry = Entry(compositeId, property, previousValue)
                  drawFormField(editor, definition, entry, id)
                } else
                  null

                if (nextValue != null) {
                  for (entry in previous)
                    if (nextValue != entry.target) {
                      val command = if (isManyToMany(editor, property))
                        Command(EditorCommands.replaceGraphValue, value = entry to Entry(singleNode, property, nextValue))
                      else
                        Command(EditorCommands.setGraphValue, value = Entry(entry.source, property, nextValue))

                      commands = commands + command
                    }
                }
              }
            }
          }
        }
        commands
      } else
        listOf<Command>()
    }
