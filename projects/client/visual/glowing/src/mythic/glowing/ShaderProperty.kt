package mythic.glowing

import org.lwjgl.BufferUtils
import mythic.spatial.Matrix
import mythic.spatial.Vector2
import mythic.spatial.Vector3m
import mythic.spatial.Vector4
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.glBindBufferRange
import org.lwjgl.opengl.GL31.*

private val matrixBuffer = BufferUtils.createFloatBuffer(16)

class MatrixProperty(private val program: ShaderProgram, name: String) {
  private val location = glGetUniformLocation(program.id, name)

  fun setValue(value: Matrix) {
    program.activate()
    glUniformMatrix4fv(location, false, value.get(matrixBuffer))
  }
}

class Vector2Property(private val program: ShaderProgram, name: String) {
  private val location = glGetUniformLocation(program.id, name)

  fun setValue(value: Vector2) {
    program.activate()
    glUniform2f(location, value.x, value.y)
  }
}

class Vector3Property(private val program: ShaderProgram, name: String) {
  private val location = glGetUniformLocation(program.id, name)

  fun setValue(value: Vector3m) {
    program.activate()
    glUniform3f(location, value.x, value.y, value.z)
  }
}

class Vector4Property(private val program: ShaderProgram, name: String) {
  private val location = glGetUniformLocation(program.id, name)

  fun setValue(value: Vector4) {
    program.activate()
    glUniform4f(location, value.x, value.y, value.z, value.w)
  }
}

class FloatProperty(private val program: ShaderProgram, name: String) {
  private val location = glGetUniformLocation(program.id, name)
  var cachedValue = Float.NaN

  fun setValue(value: Float) {
    if (cachedValue != value) {
      cachedValue = value
      program.activate()
      glUniform1f(location, value)
    }
  }
}

class UniformBufferProperty(
    private val program: ShaderProgram,
    private val name: String,
    private val bindingPoint: Int,
    uniformBuffer: UniformBuffer) {
  private val index = glGetUniformBlockIndex(program.id, name)

  init {
//    println("program: " + program.id + ", propIndex: " + index + ", ubo: " + uniformBuffer.id + ", bindingPoint: " + bindingPoint + ", name: " + name)
    assert(index != -1)
    glUniformBlockBinding(program.id, index, bindingPoint)
    glBindBufferRange(GL_UNIFORM_BUFFER, bindingPoint, uniformBuffer.id, 0, uniformBuffer.size.toLong())
  }

//  fun setValue(value: UniformBuffer) {
//    println("set program: " + program.id + ", ubo: " + index + ", size: " + value.size)
////    glBindBufferRange(GL_UNIFORM_BUFFER, 0, value.id, 0, value.size)
////    glUniformBlockBinding(program.id, index, 0)
//  }
}

