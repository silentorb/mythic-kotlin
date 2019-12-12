package mythic.bloom

import mythic.bloom.next.*
import mythic.spatial.Vector4
import mythic.typography.IndexedTextStyle

data class MenuItem(
    val name: String,
    val value: Any
)

data class Menu(
    val name: String,
    val character: String,
    val items: List<MenuItem>
)

private const val globalMenuSelectionKey = "menuSelection"

val closeMenuEvent = ClearSelectionEvent(globalMenuSelectionKey)

private val basicSelectableMenu = selectable<Menu>(globalMenuSelectionKey, optionalSingleSelection) {
  it.hashCode().toString()
}

private val childSelected: LogicModuleTransform = logicWrapper { bundle, result ->
  if (bundle.state.bag[bagMenuItemSelection] != null)
    null
  else
    result
}

private val selectableMenu: (Menu) -> LogicModuleOld = { seed ->
  childSelected(onClickPersisted(globalMenuSelectionKey, basicSelectableMenu(seed)))
}

private val invertColor = { color: Vector4 ->
  Vector4(1f - color.x, 1f - color.y, 1f - color.z, 1f)
}

private val menuFlower = { menu: Menu, style: IndexedTextStyle ->
  padding(all = 10)(label(style, menu.name))
      .plusOnClick(SelectionEvent(globalMenuSelectionKey, menu.hashCode().toString()))
//      .plusAttributes(globalMenuSelectable)
//      .plusData("id" to menu.hashCode().toString())
}

const val bagMenuItemSelection = "selectedMenuValue"

inline fun <reified T> selectedMenuValue(bag: StateBag): T? {
  val entry = bag[bagMenuItemSelection]
  return if (entry is T)
    entry
  else
    null
}

private fun selectedMenu(menu: Menu, style: IndexedTextStyle): Flower {
  val newStyle = style.copy(
      color = invertColor(style.color)
  )
  val items = menu.items.map { item ->
    padding(all = 10)(label(newStyle, item.name)).plusOnClick(closeMenuEvent, item.value)
  }
  return list(verticalPlane)(listOf(
      menuFlower(menu, newStyle) depictBehind solidBackground(style.color),
      breakReverse(list(verticalPlane, 10)(items) depictBehind solidBackground(style.color))
  ))
}

fun menuBar(style: IndexedTextStyle, menus: List<Menu>): Flower {
  val bar = list(horizontalPlane, 10)
  val items = menus.map { menu ->
    val depiction = selectableFlower(globalMenuSelectionKey, menu.hashCode().toString()) { seed, selected ->
      if (selected) {
        selectedMenu(menu, style)
      } else
        menuFlower(menu, style)
    }
    depiction
  }

  return bar(items)
}

val globalMenuModule: LogicModule =
    logicModule(newSelectionState, globalMenuSelectionKey, updateSelection(SelectionConfig(
        group = globalMenuSelectionKey,
        logic = optionalSingleSelection
    )))
