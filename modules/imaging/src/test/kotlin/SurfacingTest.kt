import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import silentorb.mythic.imaging.fathoming.cube
import silentorb.mythic.imaging.fathoming.surfacing.*
import silentorb.mythic.imaging.fathoming.surfacing.old.findSurfacingStart
import silentorb.mythic.imaging.fathoming.translate
import silentorb.mythic.spatial.Vector2
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.lineIntersectsSphere
import silentorb.mythic.spatial.projectPointFromNormal

class SurfacingTest {

  @Test()
  fun canFindStart() {
    val getDistance = cube(Vector3(2f))
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
    val getDistance = cube(Vector3(2f, 2.4f, 2.7f))
    val gridBounds = getSceneGridBounds(getDistance, 1f)
    assertEquals(-3, gridBounds.start.x)
    assertEquals(-3, gridBounds.start.y)
    assertEquals(-4, gridBounds.start.z)
    assertEquals(3, gridBounds.end.x)
    assertEquals(3, gridBounds.end.y)
    assertEquals(4, gridBounds.end.z)
  }

  @Test()
  fun lineAndSphereIntersectionWorks() {
    assertTrue(lineIntersectsSphere(Vector3(0f, 0f, 0f), Vector3(1f, 0f, 0f), Vector3(10f, 0f, 0f), 1f))
    assertTrue(lineIntersectsSphere(Vector3(0f, 0f, 0f), Vector3(1f, 0f, 0f), Vector3(-10f, 0f, 0f), 1f))
    assertTrue(lineIntersectsSphere(Vector3(0f, 0f, 0f), Vector3(1f, 0f, 0f), Vector3(0.5f, 0f, 0f), 0.1f))
    assertFalse(lineIntersectsSphere(Vector3(0f, 0f, 0f), Vector3(1f, 0f, 0f), Vector3(0.5f, 0f, 1f), 0.1f))
  }

  @Test()
  fun canSampleABoxIntersectingCellCenters() {
    val getDistance = translate(Vector3(0.4f, 0f, 0.5f), cube(Vector3(2f, 1f, 2f)))
    val config = SurfacingConfig(
        getDistance = getDistance,
        normalTolerance = 0.01f,
        cellSize = 1f,
        subCells = 4
    )
    val bounds = getSceneGridBounds(getDistance, config.cellSize)
    val sampleGrid = sampleCellGrids(config, bounds)
    val grid = sampleGrid(0)!!
    val variations = newContourGrid(getDistance, grid, config.subCells)
    val contours = isolateContours(config.normalTolerance, variations)
    val lines = detectEdges(config, contours, listOf())
    val edges = lineAggregatesToEdges(config, lines)
    val vertices = getVerticesFromEdges(edges)
    assertEquals(3, lines.size)
    assertEquals(4, vertices.size)
  }

  @Test()
  fun canSampleABoxCellIntersectingCellSides() {
    val getDistance = cube(Vector3(2f, 2f, 2f))
    val config = SurfacingConfig(
        getDistance = getDistance,
        normalTolerance = 0.01f,
        cellSize = 1f,
        subCells = 8
    )
    val bounds = getSceneGridBounds(getDistance, config.cellSize)
    val sampleGrid = sampleCellGrids(config, bounds)
    val grid = sampleGrid(0)!!
    val variations = newContourGrid(getDistance, grid, config.subCells)
    val contours = isolateContours(config.normalTolerance, variations)
    val lines = detectEdges(config, contours, listOf())
    val edges = lineAggregatesToEdges(config, lines)
    val vertices = getVerticesFromEdges(edges)
    assertEquals(3, edges.size)
    assertEquals(4, vertices.size)
  }

  @Test()
  fun canSampleABoxIntersectingCellSides() {
    val getDistance = cube(Vector3(2f, 2f, 2f))
    val config = SurfacingConfig(
        getDistance = getDistance,
        normalTolerance = 0.01f,
        cellSize = 1f,
        subCells = 32
    )
    val bounds = getSceneGridBounds(getDistance, config.cellSize)
//    val cellCount = getBoundsCellCount(bounds)
//    val traceCell = traceCellEdges(config, bounds)
//    val cellEdges = (0 until cellCount).map(traceCell)
    val edges = traceAll(bounds, config)
    val vertices = getVerticesFromEdges(edges)
    val faces = getAlignedFaces(getDistance, edges, vertices)
    assertEquals(12, edges.size)
    assertEquals(8, vertices.size)
    assertEquals(6, faces.size)
    assertTrue(faces.all { it.size == 4 })
  }

  @Test()
  fun canSampleABoxEndCell() {
    val getDistance = cube(Vector3(2f, 2f, 2f))
    val config = SurfacingConfig(
        getDistance = getDistance,
        normalTolerance = 0.01f,
        cellSize = 1f,
        subCells = 16
    )
    val bounds = getSceneGridBounds(getDistance, config.cellSize)
    val traceCell = traceCellEdges(config, bounds)
    val edges = traceCell(7)
    val vertices = getVerticesFromEdges(edges)
    assertEquals(3, edges.size)
    assertEquals(4, vertices.size)
  }

}
