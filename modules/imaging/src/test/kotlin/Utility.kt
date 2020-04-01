import org.junit.jupiter.api.Assertions.assertTrue
import silentorb.mythic.spatial.Vector3

fun assertApproximateEquals(tolerance: Float, expected: Vector3, actual: Vector3) {
  val distance = expected.distance(actual)
  assertTrue(distance <= tolerance)
}
