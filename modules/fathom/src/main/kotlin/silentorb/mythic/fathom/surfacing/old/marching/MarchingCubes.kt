package silentorb.mythic.fathom.surfacing.old.marching

import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.getNormal
import java.nio.FloatBuffer
import java.util.*

fun lerp(vec1: FloatArray, vec2: FloatArray, alpha: Float): FloatArray {
  return floatArrayOf(vec1[0] + (vec2[0] - vec1[0]) * alpha, vec1[1] + (vec2[1] - vec1[1]) * alpha, vec1[2] + (vec2[2] - vec1[2]) * alpha)
}

typealias AddTriangle = (ArrayList<Float>, Vector3, Vector3, Vector3) -> Unit

fun marchingCubes(values: FloatBuffer, volumeDimensions: Vector3i, voxelDimensions: Vector3, isoLevel: Float, addTriangle: AddTriangle): FloatArray {
  val vertexBuffer = ArrayList<Float>(64)
  // Actual position along edge weighted according to function values.
  val vertList = Array(12) { FloatArray(3) }

  // Calculate maximal possible axis value (used in vertex normalization)
  val maxX = voxelDimensions.x * (volumeDimensions.x - 1)
  val maxY = voxelDimensions.y * (volumeDimensions.y - 1)
  val maxZ = voxelDimensions.z * (voxelDimensions.z - 1)
  val maxAxisVal = Math.max(maxX, Math.max(maxY, maxZ))

  // Volume iteration
  for (z in 0 until volumeDimensions.z - 1) {
    for (y in 0 until volumeDimensions.y - 1) {
      for (x in 0 until volumeDimensions.x - 1) {

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
        val p = x + volumeDimensions.x * y + volumeDimensions.x * volumeDimensions.y * z
        val px = p + 1
        val py = p + volumeDimensions.x
        val pxy = py + 1
        val pz = p + volumeDimensions.x * volumeDimensions.y
        val pxz = px + volumeDimensions.x * volumeDimensions.y
        val pyz = py + volumeDimensions.x * volumeDimensions.y
        val pxyz = pxy + volumeDimensions.x * volumeDimensions.y

        //							  X              Y                    Z
        val position = floatArrayOf(x * voxelDimensions.x, y * voxelDimensions.y, z * voxelDimensions.z)

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
        if (bits == 0) continue

        // Interpolate the positions based od voxel intensities
        var mu = 0.5f

        // bottom of the cube
        if (bits and 1 != 0) {
          mu = ((isoLevel - value0) / (value1 - value0))
          vertList[0] = lerp(position, floatArrayOf(position[0] + voxelDimensions[0], position[1], position[2]), mu)
        }
        if (bits and 2 != 0) {
          mu = ((isoLevel - value1) / (value3 - value1))
          vertList[1] = lerp(floatArrayOf(position[0] + voxelDimensions[0], position[1], position[2]), floatArrayOf(position[0] + voxelDimensions[0], position[1] + voxelDimensions[1], position[2]), mu)
        }
        if (bits and 4 != 0) {
          mu = ((isoLevel - value2) / (value3 - value2))
          vertList[2] = lerp(floatArrayOf(position[0], position[1] + voxelDimensions[1], position[2]), floatArrayOf(position[0] + voxelDimensions[0], position[1] + voxelDimensions[1], position[2]), mu)
        }
        if (bits and 8 != 0) {
          mu = ((isoLevel - value0) / (value2 - value0))
          vertList[3] = lerp(position, floatArrayOf(position[0], position[1] + voxelDimensions[1], position[2]), mu)
        }
        // top of the cube
        if (bits and 16 != 0) {
          mu = ((isoLevel - value4) / (value5 - value4))
          vertList[4] = lerp(floatArrayOf(position[0], position[1], position[2] + voxelDimensions[2]), floatArrayOf(position[0] + voxelDimensions[0], position[1], position[2] + voxelDimensions[2]), mu)
        }
        if (bits and 32 != 0) {
          mu = ((isoLevel - value5) / (value7 - value5))
          vertList[5] = lerp(floatArrayOf(position[0] + voxelDimensions[0], position[1], position[2] + voxelDimensions[2]), floatArrayOf(position[0] + voxelDimensions[0], position[1] + voxelDimensions[1], position[2] + voxelDimensions[2]), mu)
        }
        if (bits and 64 != 0) {
          mu = ((isoLevel - value6) / (value7 - value6))
          vertList[6] = lerp(floatArrayOf(position[0], position[1] + voxelDimensions[1], position[2] + voxelDimensions[2]), floatArrayOf(position[0] + voxelDimensions[0], position[1] + voxelDimensions[1], position[2] + voxelDimensions[2]), mu)
        }
        if (bits and 128 != 0) {
          mu = ((isoLevel - value4) / (value6 - value4))
          vertList[7] = lerp(floatArrayOf(position[0], position[1], position[2] + voxelDimensions[2]), floatArrayOf(position[0], position[1] + voxelDimensions[1], position[2] + voxelDimensions[2]), mu)
        }
        // vertical lines of the cube
        if (bits and 256 != 0) {
          mu = ((isoLevel - value0) / (value4 - value0))
          vertList[8] = lerp(position, floatArrayOf(position[0], position[1], position[2] + voxelDimensions[2]), mu)
        }
        if (bits and 512 != 0) {
          mu = ((isoLevel - value1) / (value5 - value1))
          vertList[9] = lerp(floatArrayOf(position[0] + voxelDimensions[0], position[1], position[2]), floatArrayOf(position[0] + voxelDimensions[0], position[1], position[2] + voxelDimensions[2]), mu)
        }
        if (bits and 1024 != 0) {
          mu = ((isoLevel - value3) / (value7 - value3))
          vertList[10] = lerp(floatArrayOf(position[0] + voxelDimensions[0], position[1] + voxelDimensions[1], position[2]), floatArrayOf(position[0] + voxelDimensions[0], position[1] + voxelDimensions[1], position[2] + voxelDimensions[2]), mu)
        }
        if (bits and 2048 != 0) {
          mu = ((isoLevel - value2) / (value6 - value2))
          vertList[11] = lerp(floatArrayOf(position[0], position[1] + voxelDimensions[1], position[2]), floatArrayOf(position[0], position[1] + voxelDimensions[1], position[2] + voxelDimensions[2]), mu)
        }

        // construct triangles -- get correct vertices from triTable.
        var i = 0
        // "Re-purpose cubeindex into an offset into triTable."
        cubeindex = cubeindex shl 4
        while (TablesMC.MC_TRI_TABLE.get(cubeindex + i) != -1) {
          val index1: Int = TablesMC.MC_TRI_TABLE.get(cubeindex + i)
          val index2: Int = TablesMC.MC_TRI_TABLE.get(cubeindex + i + 1)
          val index3: Int = TablesMC.MC_TRI_TABLE.get(cubeindex + i + 2)

          val a = Vector3(vertList[index3][0] / maxAxisVal - 0.5f, vertList[index3][1] / maxAxisVal - 0.5f, vertList[index3][2] / maxAxisVal - 0.5f)
          val b = Vector3(vertList[index2][0] / maxAxisVal - 0.5f, vertList[index2][1] / maxAxisVal - 0.5f, vertList[index2][2] / maxAxisVal - 0.5f)
          val c = Vector3(vertList[index1][0] / maxAxisVal - 0.5f, vertList[index1][1] / maxAxisVal - 0.5f, vertList[index1][2] / maxAxisVal - 0.5f)

          addTriangle(vertexBuffer, a, b, c)

//          vertexBuffer.addAll(listOf(a.x, a.y, a.z))
//          vertexBuffer.addAll(listOf(b.x, b.y, b.z))
//          vertexBuffer.addAll(listOf(c.x, c.y, c.z))
          i += 3
        }
      }
    }
  }
  return vertexBuffer.toFloatArray()
}
