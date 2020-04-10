package silentorb.mythic.imaging.fathoming.surfacing

fun traceCellEdges(config: SurfacingConfig, bounds: GridBounds): (Int) -> Edges {
  val sampleGrid = sampleCellGrids(config, bounds)
  return { cell ->
    val grid = sampleGrid(cell)
    if (grid == null)
      listOf()
    else {
      val variations = newContourGrid(config.getDistance, grid, config.subCells + 2)
      val contours = isolateContours(config.normalTolerance, variations)
      val lines = detectEdges(config, contours, listOf())
      val edges = lineAggregatesToEdges(config, lines)
      edges
    }
  }
}

fun traceAll(config: SurfacingConfig, bounds: GridBounds, traceCell: (Int) -> Edges): Edges {
  val cellCount = getBoundsCellCount(bounds)
  val cells = (0 until cellCount).map(traceCell)
  return aggregateCells(config, bounds, cells)
}

fun traceAll(bounds: GridBounds, config: SurfacingConfig): Edges {
  val traceCell = traceCellEdges(config, bounds)
  return traceAll(config, bounds, traceCell)
}

fun traceAllSimple(config: SurfacingConfig, bounds: GridBounds, traceCell: (Int) -> Edges): Edges {
  val cellCount = getBoundsCellCount(bounds)
  val cells = (0 until cellCount).map(traceCell)
  return aggregateCellsSimple(config, cells)
}

fun traceAllSimple(bounds: GridBounds, config: SurfacingConfig): Edges {
  val traceCell = traceCellEdges(config, bounds)
  return traceAllSimple(config, bounds, traceCell)
}
