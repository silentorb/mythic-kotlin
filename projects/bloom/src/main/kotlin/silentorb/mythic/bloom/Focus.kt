package silentorb.mythic.bloom

const val focusedElementKey = "silentorb.focusedElement"
const val menuItemIndexKey = "silentorb.menuItemIndex"

fun getFocusedElement(state: BloomState): String? =
    state[focusedElementKey] as? String

fun setFocusedElement(id: String) =
    mapOf(focusedElementKey to id)

fun getFocusIndex(state: BloomState) =
    state[menuItemIndexKey] as? Int
