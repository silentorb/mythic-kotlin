package mythic.bloom

import mythic.bloom.next.Flower
import mythic.bloom.next.Seed

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
    { seed ->
      { (bloomState) ->
        val state = selectionStateOld(bloomState.bag[key])
        val id = idSelector(seed)
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

private fun childSelected2(key: String): LogicModuleTransform = logicWrapper { bundle, result ->
  if (bundle.state.bag[key] != null)
    null
  else
    result
}

fun selectableFlower(key: String, id: String, flower: (Seed, Boolean) -> Flower): Flower = { seed ->
  val state = selectionStateOld(seed.bag[key])
  val selected = state.contains(id)
  flower(seed, selected)(seed)
}
