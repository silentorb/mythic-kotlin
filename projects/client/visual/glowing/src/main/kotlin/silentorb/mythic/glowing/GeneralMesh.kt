package silentorb.mythic.glowing

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glDrawArrays
import org.lwjgl.opengl.GL11.glDrawElements
import org.lwjgl.opengl.GL14.glMultiDrawArrays
import org.lwjgl.opengl.GL31.glDrawArraysInstanced
import java.nio.FloatBuffer
import java.nio.IntBuffer

enum class PrimitiveType {
  points, // 1
  lineSegments, // 2
  triangles, // 3
  loops, // *
}

enum class DrawMethod {
  lineLoop,
  lines,
  lineStrip,
  points,
  triangles,
  triangleFan
}

fun primitiveSizes(type: PrimitiveType) =
    when (type) {
      PrimitiveType.points -> 1
      PrimitiveType.lineSegments -> 2
      PrimitiveType.triangles -> 3
      PrimitiveType.loops -> 4
    }

fun drawMethodMinimums(method: DrawMethod): Int =
    when (method) {
      DrawMethod.points -> 1
      DrawMethod.lines -> 2
      DrawMethod.triangles -> 3
      DrawMethod.triangleFan -> 4
      DrawMethod.lineLoop -> 4
      DrawMethod.lineStrip -> 4
      else -> {
        throw Error("Not supported.")
      }
    }

fun drawMethodFallbacks(type: PrimitiveType) =
    when (type) {
      PrimitiveType.points -> DrawMethod.points
      PrimitiveType.lineSegments -> DrawMethod.lines
      PrimitiveType.triangles -> DrawMethod.triangles
      PrimitiveType.loops -> throw Error("Not supported.  No fallback needed here.")
    }

fun withPossibleFallback(method: DrawMethod, type: PrimitiveType): DrawMethod {
  val size = primitiveSizes(type)
  val minimum = drawMethodMinimums(method)
  return if (size < minimum)
    drawMethodFallbacks(type)
  else
    method
}

fun createFloatBuffer(values: List<Float>): FloatBuffer {
  val buffer = BufferUtils.createFloatBuffer(values.size)
  for (value in values) {
    buffer.put(value)
  }
  buffer.flip()
  return buffer
}

fun convertDrawMethod(method: DrawMethod): Int =
    when (method) {
      DrawMethod.points -> GL11.GL_POINTS
      DrawMethod.lines -> GL11.GL_LINES
      DrawMethod.triangles -> GL11.GL_TRIANGLES
      DrawMethod.triangleFan -> GL11.GL_TRIANGLE_FAN
      DrawMethod.lineLoop -> GL11.GL_LINE_LOOP
      DrawMethod.lineStrip -> GL11.GL_LINE_STRIP
      else -> {
        throw Error("Not supported.")
      }
    }

data class GeneralMesh(
    val vertexSchema: VertexSchema,
    val vertexBuffer: VertexBuffer,
    val offsets: IntBuffer? = null,
    val counts: IntBuffer? = null,
    val indices: IntBuffer? = null,
    val count: Int? = null,
    val primitiveType: PrimitiveType
)

fun newGeneralMesh(vertexSchema: VertexSchema, primitiveType: PrimitiveType, values: List<Float>) =
    GeneralMesh(
        vertexSchema = vertexSchema,
        vertexBuffer = newVertexBuffer(vertexSchema).load(createFloatBuffer(values)),
        offsets = createIntBuffer(0),
        counts = createIntBuffer(values.size / vertexSchema.floatSize),
        primitiveType = primitiveType
    )

fun convertDrawMethod(mesh: GeneralMesh, method: DrawMethod): Int {
  val mappedMethod = if (mesh.indices != null)
    when (method) {
      DrawMethod.triangleFan -> DrawMethod.triangles
      DrawMethod.lineLoop -> DrawMethod.lineStrip
      else -> method
    }
  else
    withPossibleFallback(method, mesh.primitiveType)

  return convertDrawMethod(mappedMethod)
}

fun drawMesh(mesh: GeneralMesh, method: DrawMethod) {
  mesh.vertexBuffer.activate()
  val mappedMethod = convertDrawMethod(mesh, method)
  if (mesh.indices != null) {
    glDrawElements(mappedMethod, mesh.indices)
  } else if (mesh.offsets != null && mesh.counts != null) {
    glMultiDrawArrays(mappedMethod, mesh.offsets, mesh.counts)
  } else if (mesh.count != null) {
    glDrawArrays(mappedMethod, 0, mesh.count / mesh.vertexSchema.floatSize)
  }
}

fun drawMeshInstanced(mesh: GeneralMesh, method: DrawMethod, instanceCount: Int) {
  mesh.vertexBuffer.activate()
  val drawMode = convertDrawMethod(mesh, method)
  if (mesh.indices != null) {
    throw Error("Not implemented")
//    glDrawElements(mappedMethod, mesh.indices)
  } else {
    glDrawArraysInstanced(drawMode, 0, mesh.counts!!.get(0), instanceCount)
//    glMultiDrawArrays(mappedMethod, mesh.offsets!!, mesh.counts!!)
  }
}
