package silentorb.mythic.ent

typealias Table<T> = Map<Id, T>
typealias TableEntry<T> = Map.Entry<Id, T>

fun <T> mapTable(table: Table<T>, action: (Id, T) -> T): Table<T> =
    table.mapValues { (id, value) -> action(id, value) }

fun <T> mapTableValues(table: Table<T>, action: (T) -> T): Table<T> =
    table.mapValues { (_, value) -> action(value) }

fun <T> mapTableValues(action: (T) -> T): (Table<T>) -> Table<T> = { table ->
  table.mapValues { (_, value) -> action(value) }
}

fun <A, B> mapTableValues(table: Table<A>, secondTable: Table<B>, action: (B, A) -> A): Table<A> =
    table.mapValues { (id, value) -> action(secondTable[id]!!, value) }
