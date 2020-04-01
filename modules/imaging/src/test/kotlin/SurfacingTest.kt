import org.junit.jupiter.api.Test
import silentorb.mythic.imaging.substance.box
import silentorb.mythic.imaging.substance.surfacing.findSurfacingStart
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.projectPointFromNormal

class SurfacingTest {

  @Test()
  fun canFindStart() {
    val getDistance = box(Vector3(2f))
    val origin = Vector3(0f, -5f, 0f)
    val direction = Vector3(0f, 1f, 0f)
    val hit = findSurfacingStart(getDistance, 0.01f, origin, direction)
    assertApproximateEquals(0.01f, hit, Vector3(0f, -2f, 0f))
  }

  @Test()
  fun canProjectAlongPlanes() {
    assertApproximateEquals(0.01f,
        Vector3(0f, 20f, -10f),
        projectPointFromNormal(Vector3(1f, 0f, 0f), Vector2(10f, 20f))
    )
    assertApproximateEquals(0.01f,
        Vector3(10f, 0f, -20f),
        projectPointFromNormal(Vector3(0f, 1f, 0f), Vector2(10f, 20f))
    )
    assertApproximateEquals(0.01f,
        Vector3(20f, 7.07f, 7.07f),
        projectPointFromNormal(Vector3(0f, -1f, 1f), Vector2(20f, 10f))
    )
    assertApproximateEquals(0.01f,
        Vector3(20f, 10f, 0f),
        projectPointFromNormal(Vector3(0f, 0f, 1f), Vector2(20f, 10f))
    )
    assertApproximateEquals(0.01f,
        Vector3(-20f, 10f, 0f),
        projectPointFromNormal(Vector3(0f, 0f, -1f), Vector2(20f, 10f))
    )
  }
}
