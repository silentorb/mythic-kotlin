package silentorb.mythic.editing.panels

import imgui.ImGui
import silentorb.mythic.editing.*
import silentorb.mythic.editing.components.*
import silentorb.mythic.happenings.Command
import silentorb.mythic.happenings.Commands
import silentorb.mythic.scenery.LightType

fun getAvailableTypes(editor: Editor): List<Id> =
    getSceneFiles(editor)
        .map { sceneFileNameWithoutExtension(it.name) }
        .minus(editor.state.graph ?: "")
        .toList()

fun drawFormField(editor: Editor, definition: PropertyDefinition, entry: Entry): Any {
  return when (definition.widget!!) {
    Widgets.textureSelect -> dropDownWidget(editor.textures, entry)
    Widgets.meshSelect -> dropDownWidget(editor.meshes, entry)
    Widgets.typeSelect -> dropDownWidget(getAvailableTypes(editor), entry)
    Widgets.translation -> spatialWidget(entry)
    Widgets.rotation -> spatialWidget(entry)
    Widgets.scale -> spatialWidget(entry)
    Widgets.text -> textField("", entry)
    Widgets.decimalText -> decimalTextField("", entry)
    Widgets.rgba -> rgbaField(entry)
    Widgets.light -> dropDownWidget(LightType.values().map { it.name }, entry)
    else -> entry.target
  }
}

fun drawPropertiesPanel(editor: Editor, graph: Graph?): Commands {
  ImGui.begin("Properties")
  panelBackground()

  val result = if (graph != null) {
    var commands: Commands = listOf()
    val selection = editor.state.nodeSelection
    if (selection.size == 1) {
      val node = selection.first()
      val definitions = editor.propertyDefinitions
      val entries = getProperties(graph, node)

      ImGui.text(node)
      ImGui.separator()
      val availableDefinitions = definitions.minus(entries.map { it.property })
      val attributes = getPropertyValues<Id>(graph, node, Properties.attribute)
      if (availableDefinitions.any()) {
        val allAttributes = getCommonEditorAttributes()
        val availableAttributes = allAttributes - attributes
        if (ImGui.beginCombo("Add Property", "")) {
          for ((property, definition) in availableDefinitions) {
            if (ImGui.selectable(definition.displayName)) {
              val target = definition.defaultValue?.invoke(editor) ?: ""
              val missingDependencies = definition.dependencies - entries.map { it.property }
              val addPropertyCommands = listOf(
                  Command(EditorCommands.setGraphValue, value = Entry(node, property, target))
              )
                  .plus(
                      missingDependencies.mapNotNull {
                        val dependencyDefinition = editor.propertyDefinitions[it]
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
          for (attribute in availableAttributes) {
            if (ImGui.selectable(attribute)) {
              commands = commands.plus(Command(EditorCommands.setGraphValue, value = Entry(node, Properties.attribute, attribute)))
            }
          }
          if (commands.none()) {
            activeInputType = InputType.dropdown
          }
          ImGui.endCombo()
        }
      }

      ImGui.separator()
      for (attribute in attributes) {
        ImGui.text(attribute)
        ImGui.sameLine()
        if (ImGui.smallButton("x##attribute-$attribute")) {
          commands = commands.plus(Command(EditorCommands.removeGraphValue, value = Entry(node, Properties.attribute, attribute)))
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

  ImGui.end()

  return result
}
