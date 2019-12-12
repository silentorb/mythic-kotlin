package mythic.bloom.next

import mythic.bloom.*
import mythic.ent.pipe

typealias SelectionState = Set<BloomKey>
typealias IdSelector = (Any) -> BloomKey

val newSelectionState: SelectionState = setOf()

data class SelectionConfig(
    val group: String,
    val logic: SelectionLogic
)

data class SelectionEvent(
    val group: String,
    val item: String
)

data class ClearSelectionEvent(
    val group: String
)

fun updateSelection(config: SelectionConfig): (LogicArgs) -> (SelectionState) -> SelectionState =
    { args ->
      pipe(
          { state ->
            val events = args.events
                .filterIsInstance<ClearSelectionEvent>()
                .filter { it.group == config.group }
            if (events.any())
              setOf()
            else
              state
          },
          { state ->
            val events = args.events
                .filterIsInstance<SelectionEvent>()
                .filter { it.group == config.group }

            if (events.any()) {
              assert(events.size < 2)
              val id = events.first().item
              config.logic(state, id)
            } else
              state
          }
      )
    }
