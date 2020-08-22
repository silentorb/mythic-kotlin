package silentorb.mythic.fathom.marching

import silentorb.mythic.fathom.misc.DistanceFunction
import silentorb.mythic.fathom.misc.ShadingFunction
import silentorb.mythic.fathom.misc.getNormal
import silentorb.mythic.fathom.surfacing.GridBounds
import silentorb.mythic.fathom.surfacing.getSceneGridBounds
import silentorb.mythic.imaging.texturing.anonymousSampler
import silentorb.mythic.scenery.SamplePoint
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.toVector3

fun aggregateTriangleVertices(voxelsPerUnit: Int, offset: Vector3, getDistance: DistanceFunction, getShading: ShadingFunction, triangles: List<MarchingTriangle>): Map<MarchingEdge, SamplePoint> {
  val voxelDimensions = 1f / voxelsPerUnit.toFloat()

// This conversion to a map will reduce duplicates
  val edges = triangles
      .flatten()
      .associate { it.second to it.first }

  return edges
      .mapValues { (edge, range) ->
        val location = offset + lerp(edge.first.toVector3() * voxelDimensions, edge.second.toVector3() * voxelDimensions, range)
        SamplePoint(
            location = location,
            shading = getShading(location),
            normal = getNormal(getDistance, location),
            size = 1f,
            level = 0
        )
      }
}

fun marchingMesh(voxelsPerUnit: Int, getDistance: DistanceFunction, getShading: ShadingFunction, bounds: GridBounds): Pair<List<SamplePoint>, List<List<Int>>> {
  val voxels = voxelize(getDistance, bounds, 1, voxelsPerUnit)
  val dimensions = (bounds.end - bounds.start) * voxelsPerUnit
  val start = bounds.start.toVector3()
  val triangles = marchingCubes(voxels, dimensions, 0.5f)
  val vertexMap = aggregateTriangleVertices(voxelsPerUnit, start, getDistance, getShading, triangles)
  val vertexIndices = vertexMap.keys
      .mapIndexed { index, pair -> pair to index }
      .associate { it }
  val indexedTriangles = triangles
      .map { triangle ->
        triangle.map { vertexIndices[it.second]!! }
      }

  return vertexMap.values.toList() to indexedTriangles
}

fun marchingMesh(voxelsPerUnit: Int, getDistance: DistanceFunction, getShading: ShadingFunction): Pair<List<SamplePoint>, List<List<Int>>> {
  val bounds = getSceneGridBounds(getDistance, 1f)
      .pad(1)

  return marchingMesh(voxelsPerUnit, getDistance, getShading, bounds)
}
