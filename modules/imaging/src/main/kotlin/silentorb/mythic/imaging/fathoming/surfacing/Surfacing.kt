package silentorb.mythic.imaging.fathoming.surfacing

fun traceCellEdges(config: SurfacingConfig, bounds: GridBounds): (Int) -> SimpleEdges {
  val sampleGrid = sampleCellGrids(config, bounds)
  return { cell ->
    val grid = sampleGrid(cell)
    if (grid == null)
      listOf()
    else {
      val variations = newContourGrid(config.getDistance, grid, config.subCells + 2)
      val contours = isolateContours(config.tolerance, variations)
      val lines = detectEdges(config, contours, listOf())
      lineAggregatesToEdges(config, lines)
    }
  }
}

fun traceAll(bounds: GridBounds, traceCell: (Int) -> SimpleEdges): SimpleEdges {
  val cellCount = getBoundsCellCount(bounds)
  val cellEdges = (0 until cellCount).map(traceCell)
  return cellEdges.flatten()
}

fun traceAll(bounds: GridBounds, config: SurfacingConfig): SimpleEdges {
  val traceCell = traceCellEdges(config, bounds)
  return traceAll(bounds, traceCell)
}
