package silentorb.mythic.ent

data class GenericIdHand<Hand : Any>(
    val id: Id,
    val hand: Hand
)

fun <Hand : Any> toIdHands(nextId: IdSource, hands: List<Hand>): List<GenericIdHand<Hand>> =
    hands.map {
      GenericIdHand(
          id = nextId(),
          hand = it
      )
    }
