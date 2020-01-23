package silentorb.mythic.ent

data class GenericIdHand<Hand : Any>(
    val id: Id,
    val hand: Hand
)

fun <Hand : Any> newGenericIdHand(nextId: IdSource): (Hand) -> GenericIdHand<Hand> = { hand ->
  GenericIdHand(
      id = nextId(),
      hand = hand
  )
}

fun <Hand : Any> toIdHands(nextId: IdSource, hands: List<Hand>): List<GenericIdHand<Hand>> =
    hands.map(newGenericIdHand(nextId))
