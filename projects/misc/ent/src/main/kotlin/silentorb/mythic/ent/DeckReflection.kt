package silentorb.mythic.ent

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

data class DeckReflection<Deck : Any, Hand : Any>(
    val deckType: KClass<Deck>,
    val deckConstructor: KFunction<Deck>,
    val deckProperties: List<KProperty1<Deck, *>>,
    val handType: KClass<Hand>,
    val handProperties: List<KProperty1<Hand, *>>
)

fun <Deck : Any, Hand : Any> newDeckReflection(deckType: KClass<Deck>, handType: KClass<Hand>): DeckReflection<Deck, Hand> {
  val deckConstructor = deckType.constructors.first()
  val deckProperties = deckConstructor.parameters
      .map { p -> deckType.memberProperties.first { it.name == p.name } }
  val handProperties = deckConstructor.parameters
      .map { p ->
        handType.memberProperties.first {
          it.returnType.jvmErasure.jvmName == p.type.arguments[1].type!!.jvmErasure.jvmName
        }
      }
  return DeckReflection(
      deckType = deckType,
      deckConstructor = deckConstructor,
      deckProperties = deckProperties,
      handType = handType,
      handProperties = handProperties
  )
}

fun <Deck> newReflectedDeck(deckConstructor: KFunction<Deck>, args: List<Table<Any>> = listOf()): Deck =
    deckConstructor.call(*args.toTypedArray())

fun <Deck : Any, Hand : Any> genericRemoveEntities(deckReflection: DeckReflection<Deck, Hand>): (Set<Id>) -> (Deck) -> Deck =
    { removeIds ->
      { deck ->
        val isActive = { id: Id -> !removeIds.contains(id) }
        val deletions = deckReflection.deckProperties.map { property ->
          val value = property.get(deck) as Table<Any>
          value.filterKeys(isActive)
        }
        newReflectedDeck(deckReflection.deckConstructor, deletions)
      }
    }

fun <Deck : Any, Hand : Any> genericMergeDecks(deckReflection: DeckReflection<Deck, Hand>): (Deck, Deck) -> Deck = { first, second ->
  val additions = deckReflection.deckProperties.map { property ->
    val first = property.get(first) as Table<Any>
    val second = property.get(second) as Table<Any>
    first.plus(second)
  }
  newReflectedDeck(deckReflection.deckConstructor, additions)
}

fun <T : WithId> nullableList(entity: T?): Table<T> =
    if (entity == null)
      mapOf()
    else
      mapOf(entity.id to entity)

fun <T> nullableList(id: Id, entity: T?): Table<T> =
    if (entity == null)
      mapOf()
    else
      mapOf(id to entity)

fun <Deck : Any, Hand : Any> genericHandToDeck(deckReflection: DeckReflection<Deck, Hand>): (Id, Hand) -> Deck = { id, hand ->
  val additions = deckReflection.handProperties.map { property ->
    val value = property.get(hand) as Any?
    nullableList(id, value)
  }
  newReflectedDeck(deckReflection.deckConstructor, additions)
}

fun <Deck : Any, Hand : Any> genericHandToDeckWithIdSource(deckReflection: DeckReflection<Deck, Hand>): (IdSource, Hand) -> Deck = { nextId, hand ->
  val id = nextId()
  val additions = deckReflection.handProperties.map { property ->
    val value = property.get(hand) as Any?
    nullableList(id, value)
  }
  val deck = newReflectedDeck(deckReflection.deckConstructor, additions)
  deck
}

fun <Deck : Any, Hand : Any> genericIdHandsToDeck(deckReflection: DeckReflection<Deck, Hand>): (List<GenericIdHand<Hand>>) -> Deck = { hands ->
  val additions = deckReflection.handProperties.map { property ->
    hands.mapNotNull { hand ->
      val value = property.get(hand.hand)
      if (value != null)
        Pair(hand.id, value)
      else
        null
    }.associate { it }
  }
  val deck = newReflectedDeck(deckReflection.deckConstructor, additions)
  deck
}

fun <Deck : Any, Hand : Any> genericAllHandsOnDeck(deckReflection: DeckReflection<Deck, Hand>): (List<Hand>, IdSource, Deck) -> Deck = { hands, nextId, deck ->
  hands.fold(deck, { d, h -> genericMergeDecks(deckReflection)(d, genericHandToDeckWithIdSource(deckReflection)(nextId, h)) })
}
