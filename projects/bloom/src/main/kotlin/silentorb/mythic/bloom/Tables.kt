package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

fun tableFlower(rows: List<List<Flower>>): Flower = { seed ->
  val boxes = rows.map { row ->
    row.map { cell -> cell(seed) }
  }

  Box(
      dimensions = Vector2i(),
      boxes = rows.map { row ->
        row.map { cell ->
          OffsetBox(
              offset = Vector2i.zero,
              child = cell(seed),
          )
        }
      }
          .flatten()
  )
}
