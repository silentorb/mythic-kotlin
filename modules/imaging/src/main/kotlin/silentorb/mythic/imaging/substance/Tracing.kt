package silentorb.mythic.imaging.substance

import silentorb.mythic.imaging.substance.surfacing.*
import silentorb.mythic.spatial.Vector3

fun traceCellContours(config: SurfacingConfig, center: Vector3, bounds: DecimalBounds): ContourMesh? {
  val getDistance = config.getDistance
  val normal = calculateNormal(getDistance, center)
  return null
}

fun traceContours(config: SurfacingConfig, bounds: GridBounds, scale: Float): ContourMesh {
  val start = bounds.start
  val end = bounds.end
  val dimensions = end - start
  val halfCell = scale / 2f
  val cellCount = dimensions.x * dimensions.y * dimensions.z
  val cells = Array<ContourMesh?>(cellCount) { null }
  var i = 0
  val maxCellRange = Vector3(halfCell).length()
  for (z in start.x until end.z) {
    for (y in start.y until end.y) {
      for (x in start.z until end.x) {
        val center = Vector3(x.toFloat() * scale, y.toFloat() * scale, z.toFloat() * scale)

        // Skip cells that have no geometry
        val rangeSample = config.getDistance(center)
        if (rangeSample <= maxCellRange) {
          val cellBounds = DecimalBounds(
              center - halfCell,
              center + halfCell
          )
          val cellMesh = traceCellContours(config, center, cellBounds)
          cells[i] = cellMesh
        }
        i++
      }
    }
  }

  return cells
      .filterNotNull()
      .reduce(mergeContourMeshes(config.tolerance))
}
