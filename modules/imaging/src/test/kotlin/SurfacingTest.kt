import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import silentorb.mythic.imaging.substance.box
import silentorb.mythic.imaging.substance.surfacing.SurfacingConfig
import silentorb.mythic.imaging.substance.surfacing.getSceneGridBounds
import silentorb.mythic.imaging.substance.surfacing.old.findSurfacingStart
import silentorb.mythic.imaging.substance.surfacing.sampleCellGrids
import silentorb.mythic.imaging.substance.surfacing.traceContours
import silentorb.mythic.imaging.substance.translate
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

  @Test()
  fun canGetBounds() {
    val getDistance = box(Vector3(2f, 2.4f, 2.7f))
    val gridBounds = getSceneGridBounds(getDistance, 1f)
    assertEquals(-3, gridBounds.start.x)
    assertEquals(-3, gridBounds.start.y)
    assertEquals(-4, gridBounds.start.z)
    assertEquals(3, gridBounds.end.x)
    assertEquals(3, gridBounds.end.y)
    assertEquals(4, gridBounds.end.z)
  }

  @Test()
  fun canSampleABoxIntersectingCellCenters() {
    val getDistance = translate(Vector3(0.5f, 0f, 0.5f), box(Vector3(2f, 1f, 2f)))
    val config = SurfacingConfig(
        getDistance = getDistance,
        tolerance = 0.01f,
        cellSize = 1f,
        subCells = 4
    )
    val bounds = getSceneGridBounds(getDistance, config.cellSize)
    val sampleGrid = sampleCellGrids(config, bounds)
    val grid = sampleGrid(0)
    val k = 0
  }

  @Test()
  fun canTraceABoxIntersectingCellCenters() {
    val getDistance = box(Vector3(2.5f))
    val config = SurfacingConfig(
        getDistance = getDistance,
        tolerance = 0.01f,
        cellSize = 1f,
        subCells = 4
    )
    val bounds = getSceneGridBounds(getDistance, config.cellSize).pad(1)
    val mesh = traceContours(config, bounds)
    assertEquals(8, mesh.vertices.size)
    val k = 0
  }
}