package silentorb.mythic.bloom.old

import silentorb.mythic.bloom.BloomKey
import silentorb.mythic.bloom.existingOrNewState

typealias GenericIdSelector<T> = (T) -> BloomKey

data class SelectionStateOld(
    val selection: Set<BloomKey>
)

val selectionStateOld = existingOrNewState {
  setOf<String>()
}

val singleSelectionState = existingOrNewState {
  SelectionStateOld(
      selection = setOf()
  )
}

typealias SelectionLogic = (Set<BloomKey>, BloomKey) -> Set<BloomKey>

//val singleSelection: SelectionLogic = { _, item ->
//  setOf(item)
//}

val optionalSingleSelection: SelectionLogic = { selection, item ->
  if (selection.contains(item))
    setOf()
  else
    setOf(item)
}

fun <T> selectable(key: String, selectionLogic: SelectionLogic, idSelector: GenericIdSelector<T>): (T) -> LogicModuleOld =
    { dimensions ->
      { (bloomState) ->
        val state = selectionStateOld(bloomState.resourceBag[key])
        val id = idSelector(dimensions)
        val selection = selectionLogic(state, id)
        if (selection.none())
          mapOf(key to setOf<String>())
        else {
          val newState = SelectionStateOld(
              selection = selection
          )

          mapOf(key to newState)
        }
      }
    }
