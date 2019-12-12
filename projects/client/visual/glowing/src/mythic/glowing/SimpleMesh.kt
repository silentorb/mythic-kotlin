package mythic.glowing

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.glMultiDrawArrays
import org.lwjgl.opengl.GL31.glDrawArraysInstanced
import java.nio.FloatBuffer
import java.nio.IntBuffer

enum class DrawMethod {
  lineLoop,
  lines,
  lineStrip,
  points,
  triangles,
  triangleFan
}

fun createFloatBuffer(values: List<Float>): FloatBuffer {
  val buffer = BufferUtils.createFloatBuffer(values.size)
  for (value in values) {
    buffer.put(value)
  }
  buffer.flip()
  return buffer
}

fun createIntBuffer(value: Int): IntBuffer {
  val buffer = BufferUtils.createIntBuffer(1)
  buffer.put(value)
  buffer.flip()
  return buffer
}

fun convertDrawMethod(method: DrawMethod): Int {
  when (method) {
    DrawMethod.triangles -> return GL_TRIANGLES
    DrawMethod.triangleFan -> return GL_TRIANGLE_FAN
    DrawMethod.lineLoop -> return GL_LINE_LOOP
    DrawMethod.lineStrip -> return GL_LINE_STRIP
    DrawMethod.lines -> return GL_LINES
    DrawMethod.points -> return GL_POINTS

    else -> {
      throw Error("Not supported.")
    }
  }
}

interface Drawable {
  fun draw(method: DrawMethod)
  fun dispose()
}

data class GeneralMesh(
    val vertexSchema: VertexSchema,
    val vertexBuffer: VertexBuffer,
    val offsets: IntBuffer? = null,
    val counts: IntBuffer? = null,
    val indices: IntBuffer? = null
)

fun newGeneralMesh(vertexSchema: VertexSchema, values: List<Float>) =
    GeneralMesh(
        vertexSchema = vertexSchema,
        vertexBuffer = newVertexBuffer(vertexSchema).load(createFloatBuffer(values)),
        offsets = createIntBuffer(0),
        counts = createIntBuffer(values.size / vertexSchema.floatSize)
    )

fun convertDrawMethod(mesh: GeneralMesh, method: DrawMethod): Int {
  val mappedMethod = if (mesh.indices != null) {
    when (method) {
      DrawMethod.triangleFan -> DrawMethod.triangles
      DrawMethod.lineLoop -> DrawMethod.lineStrip
      else -> method
    }
  } else
    method

  return convertDrawMethod(mappedMethod)
}

fun drawMesh(mesh: GeneralMesh, method: DrawMethod) {
  mesh.vertexBuffer.activate()
  val mappedMethod = convertDrawMethod(mesh, method)
  if (mesh.indices != null) {
    glDrawElements(mappedMethod, mesh.indices)
  } else {
    glMultiDrawArrays(mappedMethod, mesh.offsets!!, mesh.counts!!)
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

class SimpleMesh(val vertexBuffer: VertexBuffer, val offsets: IntBuffer, val counts: IntBuffer) : Drawable {

  override fun draw(method: DrawMethod) {
    vertexBuffer.activate()
    glMultiDrawArrays(convertDrawMethod(method), offsets, counts)
  }

  fun drawElement(method: DrawMethod, index: Int) {
    vertexBuffer.activate()
    glDrawArrays(convertDrawMethod(method), offsets[index], counts[index])
  }

  constructor(vertexSchema: VertexSchema, values: List<Float>) :
      this(newVertexBuffer(vertexSchema),
          createIntBuffer(0),
          createIntBuffer(values.size / vertexSchema.floatSize)) {
    vertexBuffer.load(createFloatBuffer(values))
  }

  constructor(vertexSchema: VertexSchema, buffer: FloatBuffer, offsets: IntBuffer, counts: IntBuffer) :
      this(newVertexBuffer(vertexSchema), offsets, counts) {
    vertexBuffer.load(buffer)
  }

  override fun dispose() {
    vertexBuffer.dispose()
  }
}

class SimpleTriangleMesh(val vertexBuffer: VertexBuffer, val indices: IntBuffer) : Drawable {

  override fun draw(method: DrawMethod) {
    vertexBuffer.activate()
    val convertedMethod = when (method) {
      DrawMethod.triangleFan -> DrawMethod.triangles
      DrawMethod.lineLoop -> DrawMethod.lineStrip
      else -> method
    }
    glDrawElements(convertDrawMethod(convertedMethod), indices)
  }

  constructor(vertexSchema: VertexSchema, buffer: FloatBuffer, indices: IntBuffer) :
      this(newVertexBuffer(vertexSchema), indices) {
    vertexBuffer.load(buffer)
  }

  override fun dispose() {
    vertexBuffer.dispose()
  }
}

class MutableSimpleMesh(val vertexSchema: VertexSchema) : Drawable {
  var offsets: IntBuffer = BufferUtils.createIntBuffer(1)
  var counts: IntBuffer = BufferUtils.createIntBuffer(1)
  private val vertexBuffer = newVertexBuffer(vertexSchema)
  private var floatBuffer = BufferUtils.createFloatBuffer(64)
  private var custodian = FloatBufferCustodian(floatBuffer)

  override fun draw(method: DrawMethod) {
    vertexBuffer.activate()
    glMultiDrawArrays(convertDrawMethod(method), offsets, counts)
  }

  fun load(values: List<Float>) {
    if (values.size > floatBuffer.capacity()) {
      floatBuffer = BufferUtils.createFloatBuffer(values.size)
      custodian = FloatBufferCustodian(floatBuffer)
    }

    for (value in values) {
      floatBuffer.put(value)
    }
    custodian.finish()
    vertexBuffer.load(floatBuffer)
    offsets.put(0)
    offsets.flip()
    counts.put(values.size / vertexSchema.floatSize)
    counts.flip()
  }

  override fun dispose() {
    vertexBuffer.dispose()
  }
}
