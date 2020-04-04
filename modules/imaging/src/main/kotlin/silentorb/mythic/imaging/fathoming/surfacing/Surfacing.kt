package silentorb.mythic.imaging.fathoming.surfacing

fun traceCellEdges(config: SurfacingConfig, bounds: GridBounds): (Int) -> SimpleEdges {
    val sampleGrid = sampleCellGrids(config, bounds)
    return { cell ->
        val grid = sampleGrid(cell)
        if(grid == null)
            listOf()
        else {
            val variations = newContourGrid(config.getDistance, grid, config.subCells)
            val contours = isolateContours(config.tolerance, variations)
            val lines = detectEdges(config, contours, listOf())
            lineAggregatesToEdges(0.01f, lines)
        }
    }
}
