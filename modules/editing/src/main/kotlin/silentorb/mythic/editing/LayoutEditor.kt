package silentorb.mythic.editing

import imgui.ImGui
import silentorb.mythic.editing.components.*
import silentorb.mythic.editing.panels.*
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.happenings.Commands

fun drawEditor(editor: Editor, deviceStates: List<InputDeviceState>): Commands {
  val graph = getActiveEditorGraph(editor)
  val menuCommands = drawMainMenuBar(newMenuChannel(editor, Contexts.global), listOf(Contexts.global))

  ImGui.setNextWindowBgAlpha(0f)
  ImGui.dockSpaceOverViewport()

  drawEditor3dElements(editor)

  val panelResponses =
      listOf(
          renderTree(editor, graph),
          renderProject(editor),
          drawPropertiesPanel(editor, graph),
          drawViewportPanel(editor),
      )

  val panelMenuCommands = panelResponses.flatMap { it.second }
  val context = panelResponses.fold(Contexts.global) { a, b -> b.first ?: a }

  val shortcutCommands = getShortcutCommands(editor.bindings, context, deviceStates)

  val menuAndPanelCommands = menuCommands + panelMenuCommands + shortcutCommands

  val dialogCommands = listOf(
      newNodeNameDialog,
      renameNodeDialog(editor),
      renameFileItemDialog(editor),
      newFileNameDialog,
      newFolderNameDialog
  )
      .flatMap { it(menuAndPanelCommands) }


  return menuAndPanelCommands + dialogCommands + getImGuiCommands(editor)
}

fun mapMenu(parent: PathList, index: Int, menuItem: MenuTree): ContextMenus {
  val key = menuItem.key ?: menuItem.commandType ?: menuItem.label
  val path = parent + key
  val items = menuItem.items
  return mapOf(
      path to MenuItem(
          label = menuItem.label,
          commandType = menuItem.commandType,
          command = menuItem.command,
          getState = menuItem.getState,
          weight = index * 10
      ),
  ) + if (items != null)
    items.mapIndexed { index, child ->
      mapMenu(path, index, child)
    }
        .fold(mapOf()) { a, b -> a + b }
  else
    mapOf()
}

fun panelMenus(): ContextMenus = mapOf(
    Contexts.global to mainMenus(),
    Contexts.project to projectMenus(),
    Contexts.nodes to sceneTreeMenus(),
    Contexts.properties to propertiesMenus(),
    Contexts.viewport to viewportMenus(),
)
    .entries
    .flatMap { b -> b.value.mapIndexed { index, item -> mapMenu(listOf(b.key), index, item) } }
    .fold(mapOf()) { a, b -> a + b }
