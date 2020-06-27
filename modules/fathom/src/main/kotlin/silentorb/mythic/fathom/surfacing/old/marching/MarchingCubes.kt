package silentorb.mythic.fathom.surfacing.old.marching

import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector3i
import java.util.*

fun lerp(vec1: Vector3, vec2: Vector3, alpha: Float): Vector3 =
    vec1 + (vec2 - vec1) * alpha

typealias AddTriangle = (ArrayList<Float>, Vector3, Vector3, Vector3) -> Unit

fun marchingCubes(values: FloatArray, start: Vector3, volumeDimensions: Vector3i, voxelDimensions: Vector3, isoLevel: Float, addTriangle: AddTriangle): FloatArray {
  val vertexBuffer = ArrayList<Float>(64)
  val vertList = Array(12) { Vector3.zero }

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

        val position = Vector3(x.toFloat(), y.toFloat(), z.toFloat()) * voxelDimensions.x

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
          vertList[0] = lerp(position, Vector3(position.x + voxelDimensions.x, position.y, position.z), mu)
        }
        if (bits and 2 != 0) {
          mu = ((isoLevel - value1) / (value3 - value1))
          vertList[1] = lerp(Vector3(position.x + voxelDimensions.x, position.y, position.z), Vector3(position.x + voxelDimensions.x, position.y + voxelDimensions.y, position.z), mu)
        }
        if (bits and 4 != 0) {
          mu = ((isoLevel - value2) / (value3 - value2))
          vertList[2] = lerp(Vector3(position.x, position.y + voxelDimensions.y, position.z), Vector3(position.x + voxelDimensions.x, position.y + voxelDimensions.y, position.z), mu)
        }
        if (bits and 8 != 0) {
          mu = ((isoLevel - value0) / (value2 - value0))
          vertList[3] = lerp(position, Vector3(position.x, position.y + voxelDimensions.y, position.z), mu)
        }
        // top of the cube
        if (bits and 16 != 0) {
          mu = ((isoLevel - value4) / (value5 - value4))
          vertList[4] = lerp(Vector3(position.x, position.y, position.z + voxelDimensions.z), Vector3(position.x + voxelDimensions.x, position.y, position.z + voxelDimensions.z), mu)
        }
        if (bits and 32 != 0) {
          mu = ((isoLevel - value5) / (value7 - value5))
          vertList[5] = lerp(Vector3(position.x + voxelDimensions.x, position.y, position.z + voxelDimensions.z), Vector3(position.x + voxelDimensions.x, position.y + voxelDimensions.y, position.z + voxelDimensions.z), mu)
        }
        if (bits and 64 != 0) {
          mu = ((isoLevel - value6) / (value7 - value6))
          vertList[6] = lerp(Vector3(position.x, position.y + voxelDimensions.y, position.z + voxelDimensions.z), Vector3(position.x + voxelDimensions.x, position.y + voxelDimensions.y, position.z + voxelDimensions.z), mu)
        }
        if (bits and 128 != 0) {
          mu = ((isoLevel - value4) / (value6 - value4))
          vertList[7] = lerp(Vector3(position.x, position.y, position.z + voxelDimensions.z), Vector3(position.x, position.y + voxelDimensions.y, position.z + voxelDimensions.z), mu)
        }
        // vertical lines of the cube
        if (bits and 256 != 0) {
          mu = ((isoLevel - value0) / (value4 - value0))
          vertList[8] = lerp(position, Vector3(position.x, position.y, position.z + voxelDimensions.z), mu)
        }
        if (bits and 512 != 0) {
          mu = ((isoLevel - value1) / (value5 - value1))
          vertList[9] = lerp(Vector3(position.x + voxelDimensions.x, position.y, position.z), Vector3(position.x + voxelDimensions.x, position.y, position.z + voxelDimensions.z), mu)
        }
        if (bits and 1024 != 0) {
          mu = ((isoLevel - value3) / (value7 - value3))
          vertList[10] = lerp(Vector3(position.x + voxelDimensions.x, position.y + voxelDimensions.y, position.z), Vector3(position.x + voxelDimensions.x, position.y + voxelDimensions.y, position.z + voxelDimensions.z), mu)
        }
        if (bits and 2048 != 0) {
          mu = ((isoLevel - value2) / (value6 - value2))
          vertList[11] = lerp(Vector3(position.x, position.y + voxelDimensions.y, position.z), Vector3(position.x, position.y + voxelDimensions.y, position.z + voxelDimensions.z), mu)
        }

        var i = 0
        val nextCubeIndex = cubeindex shl 4
        while (TablesMC.MC_TRI_TABLE.get(nextCubeIndex + i) != -1) {
          val index1: Int = TablesMC.MC_TRI_TABLE.get(nextCubeIndex + i)
          val index2: Int = TablesMC.MC_TRI_TABLE.get(nextCubeIndex + i + 1)
          val index3: Int = TablesMC.MC_TRI_TABLE.get(nextCubeIndex + i + 2)

          val a = vertList[index3] + start
          val b = vertList[index2] + start
          val c = vertList[index1] + start

          addTriangle(vertexBuffer, a, b, c)

          i += 3
        }
      }
    }
  }
  return vertexBuffer.toFloatArray()
}
