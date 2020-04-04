package silentorb.mythic.glowing

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14.glMultiDrawArrays
import org.lwjgl.opengl.GL31.glDrawArraysInstanced
import java.nio.FloatBuffer
import java.nio.IntBuffer

interface Drawable {
  fun draw(method: DrawMethod)
  fun dispose()
}

 // Deprecated in favor of GeneralMesh
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
