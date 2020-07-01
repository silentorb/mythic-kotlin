package silentorb.mythic.fathom.marching

import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i

fun lerp(vec1: Vector3, vec2: Vector3, alpha: Float): Vector3 =
    vec1 + (vec2 - vec1) * alpha

fun lerp(a: Float, b: Float, alpha: Float): Float =
    a + (b - a) * alpha

typealias MarchingEdge = Pair<Vector3i, Vector3i>
typealias IntermediateMarchingEdge = Pair<Float, MarchingEdge>
typealias MarchingTriangle = List<IntermediateMarchingEdge>

tailrec fun marchTriangles(getVertex: (Int) -> IntermediateMarchingEdge, i: Int, accumulator: List<MarchingTriangle>): List<MarchingTriangle> =
    if (TablesMC.MC_TRI_TABLE.get(i) == -1)
      accumulator
    else {
      val triangle = listOf(0, 1, 2).map {
        val index = TablesMC.MC_TRI_TABLE.get(i + it)
        getVertex(index)
      }
      marchTriangles(getVertex, i + 3, accumulator.plusElement(triangle))
    }

fun marchingCubes(values: FloatArray, dimensions: Vector3i, isoLevel: Float): List<MarchingTriangle> =
    (0 until dimensions.z - 1).flatMap { z ->
      (0 until dimensions.y - 1).flatMap { y ->
        (0 until dimensions.x - 1).flatMap { x ->

          // Indices pointing to cube vertices
          //              pyz  ___________________  pxyz
          //                  /|                 /|
          //                 / |                / |
          //                /  |               /  |
          //          pz   /___|______________/pxz|
          //              |    |              |   |
          //              |    |              |   |
          //              | py |______________|___| pxy
          //              |   /               |   /
          //              |  /                |  /
          //              | /                 | /
          //              |/__________________|/
          //             p                     px
          val p = x + dimensions.x * y + dimensions.x * dimensions.y * z
          val px = p + 1
          val py = p + dimensions.x
          val pxy = py + 1
          val pz = p + dimensions.x * dimensions.y
          val pxz = px + dimensions.x * dimensions.y
          val pyz = py + dimensions.x * dimensions.y
          val pxyz = pxy + dimensions.x * dimensions.y

          // Voxel intensities
          val value0 = values[p]
          val value1 = values[px]
          val value2 = values[py]
          val value3 = values[pxy]
          val value4 = values[pz]
          val value5 = values[pxz]
          val value6 = values[pyz]
          val value7 = values[pxyz]

          // Voxel is active if its intensity is above isolevel
          var cubeindex = 0
          if (value0 > isoLevel) cubeindex = cubeindex or 1
          if (value1 > isoLevel) cubeindex = cubeindex or 2
          if (value2 > isoLevel) cubeindex = cubeindex or 8
          if (value3 > isoLevel) cubeindex = cubeindex or 4
          if (value4 > isoLevel) cubeindex = cubeindex or 16
          if (value5 > isoLevel) cubeindex = cubeindex or 32
          if (value6 > isoLevel) cubeindex = cubeindex or 128
          if (value7 > isoLevel) cubeindex = cubeindex or 64

          // Fetch the triggered edges
          val bits: Int = TablesMC.MC_EDGE_TABLE.get(cubeindex)

          // If no edge is triggered... skip
          if (bits == 0)
            listOf()
          else {
            fun getVertex(index: Int): IntermediateMarchingEdge {
              val initial = when (index) {
                0 -> (isoLevel - value0) / (value1 - value0) to (Vector3i(x, y, z) to Vector3i(x + 1, y, z))
                1 -> (isoLevel - value1) / (value3 - value1) to (Vector3i(x + 1, y, z) to Vector3i(x + 1, y + 1, z))
                2 -> (isoLevel - value2) / (value3 - value2) to (Vector3i(x, y + 1, z) to Vector3i(x + 1, y + 1, z))
                3 -> (isoLevel - value0) / (value2 - value0) to (Vector3i(x, y, z) to Vector3i(x, y + 1, z))
                4 -> (isoLevel - value4) / (value5 - value4) to (Vector3i(x, y, z + 1) to Vector3i(x + 1, y, z + 1))
                5 -> (isoLevel - value5) / (value7 - value5) to (Vector3i(x + 1, y, z + 1) to Vector3i(x + 1, y + 1, z + 1))
                6 -> (isoLevel - value6) / (value7 - value6) to (Vector3i(x, y + 1, z + 1) to Vector3i(x + 1, y + 1, z + 1))
                7 -> (isoLevel - value4) / (value6 - value4) to (Vector3i(x, y, z + 1) to Vector3i(x, y + 1, z + 1))
                8 -> (isoLevel - value0) / (value4 - value0) to (Vector3i(x, y, z) to Vector3i(x, y, z + 1))
                9 -> (isoLevel - value1) / (value5 - value1) to (Vector3i(x + 1, y, z) to Vector3i(x + 1, y, z + 1))
                10 -> (isoLevel - value3) / (value7 - value3) to (Vector3i(x + 1, y + 1, z) to Vector3i(x + 1, y + 1, z + 1))
                11 -> (isoLevel - value2) / (value6 - value2) to (Vector3i(x, y + 1, z) to Vector3i(x, y + 1, z + 1))
                else -> throw Error("Not supported")
              }
              // Normalize the edges so that the same edge always has the same vertex order and thus the same hashcode for later congruency
              return if (initial.first.hashCode() > initial.second.hashCode())
                (1f - initial.first) to (initial.second.second to initial.second.first)
              else
                initial
            }

            val nextCubeIndex = cubeindex shl 4
            marchTriangles(::getVertex, nextCubeIndex, listOf())
          }
        }
      }
    }

//                start + lerp(data.second.first.toVector3() * voxelDimensions, data.second.second.toVector3() * voxelDimensions, data.first)
