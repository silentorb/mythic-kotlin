package silentorb.mythic.imaging.fathoming.surfacing.old

import silentorb.mythic.imaging.fathoming.surfacing.Contours
import kotlin.math.abs

tailrec fun groupDuplicates(tolerance: Float, contours: Contours, duplicates: List<Contours>): List<Contours> {
  return if (contours.none())
    duplicates
  else {
    val next = contours.first()
    val remaining = contours.drop(1)
    val matches = remaining.filter {
      it.position.distance(next.position) < tolerance
    }
    val nextDuplicates = if (matches.any())
      duplicates.plusElement(listOf(next).plus(matches))
    else
      duplicates

    val nextContours = remaining.minus(matches)

    groupDuplicates(tolerance, nextContours, nextDuplicates)
  }
}

fun groupDuplicates(tolerance: Float, contours: Contours) =
    groupDuplicates(tolerance, contours, listOf())

data class DuplicateResult(
    val main: Contours,
    val pivots: Contours
)

// There are two kinds of duplicates: pure duplicates and pivot duplicates.
// Pivot duplicates contain a mix of directions and are intersections between multiple lines.
// Pivot direction is unreliable so pivots shouldn't be used as the start of the line but can
// be used as the end of a line.
// Pure duplicates have identical facing and should be merged into a single entry.
// Pure duplicates are candidates for the start of a line.
fun removeDuplicates(contours: Contours, groups: List<Contours>): DuplicateResult {
  val (pureDuplicates, pivots) = groups.partition { group ->
    val first = group.first()
    val others = group.drop(1)
    others.all { other -> abs(first.direction.dot(other.direction)) > 0.82f }
  }
  val shouldBeRemoved = pivots.flatten().plus(pureDuplicates.flatMap { it.drop(1) })
  return DuplicateResult(
      main = contours.minus(shouldBeRemoved),
      pivots = pivots.map { it.first() }
  )
}

fun removeDuplicates(tolerance: Float, contours: Contours): DuplicateResult {
  val groups = groupDuplicates(tolerance, contours)
  return removeDuplicates(contours, groups)
}
