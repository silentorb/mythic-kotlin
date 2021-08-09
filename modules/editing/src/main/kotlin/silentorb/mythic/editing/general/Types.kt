package silentorb.mythic.editing.general

import silentorb.mythic.editing.main.ContextMenus
import silentorb.mythic.editing.main.Editor
import silentorb.mythic.editing.main.GetMenuItemState
import silentorb.mythic.happenings.Command
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector2i

data class Typeface(
    val name: String,
    val path: String,
    val size: Float
)

data class ContextCommand(
    val context: String,
    val command: String,
    val menu: String? = null
)

typealias KeystrokeBindings = Map<ContextCommand, String>
typealias CompressedKeystrokeBindings = Map<Int, List<ContextCommand>>

typealias GetShortcut = (String) -> String?

data class MenuChannel(
    val getShortcut: GetShortcut,
    val editor: Editor,
    val menus: ContextMenus,
)

data class MenuTree(
    val label: String,
    val commandType: String? = null,
    val command: Command? = null,
    val items: List<MenuTree>? = null,
    val key: String? = null,
    val getState: GetMenuItemState? = null,
)

data class Clipboard(
    val type: String,
    val data: Any,
)

data class MouseState(
    val position: Vector2i,
    val offset: Vector2,
)
