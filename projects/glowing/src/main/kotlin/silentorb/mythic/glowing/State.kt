package silentorb.mythic.glowing

import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.glBindBuffer

import silentorb.mythic.spatial.Vector4
import silentorb.mythic.spatial.Vector4i
import org.lwjgl.opengl.GL13.GL_MULTISAMPLE
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.opengl.GL14.GL_BLEND_DST_RGB
import org.lwjgl.opengl.GL14.GL_BLEND_SRC_RGB
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER
import org.lwjgl.opengl.GL32

fun getGLBounds(type: Int): Vector4i {
  val buffer = IntArray(4)
  glGetIntegerv(type, buffer)
  return Vector4i(buffer[0], buffer[1], buffer[2], buffer[3])
}

private fun setEnabled(register: Int, value: Boolean) {
  if (value)
    glEnable(register)
  else
    glDisable(register)
}

enum class GlField {
  depthEnabled,
  depthTest,
  depthWrite,
  viewport,
}

typealias GlStateMap = Map<GlField, Any>

fun setGlState(field: GlField, value: Any) {
  when (field) {
    GlField.depthTest -> setEnabled(GL_DEPTH_TEST, value as Boolean)
    GlField.depthWrite -> glDepthMask(value as Boolean)
    GlField.viewport -> {
      value as Vector4i
      glViewport(value.x, value.y, value.z, value.w)
    }
  }
}

class State {
  val data: MutableMap<GlField, Any> = mutableMapOf(
      GlField.depthTest to false,
      GlField.depthWrite to false,
      GlField.viewport to getGLBounds(GL_VIEWPORT),
  )

  var clearColor: Vector4 = Vector4(0f, 0f, 0f, 1f)
    set(value) {
      if (field != value) {
        field = value
        glClearColor(value.x, value.y, value.z, value.w)
      }
    }

  var vertexArrayObject: Int = 0
    set(value) {
      if (field != value) {
        field = value
        glBindVertexArray(value)
      }
    }

  var vertexBufferObject: Int = 0
    set(value) {
      if (field != value) {
        field = value
        glBindBuffer(GL_ARRAY_BUFFER, value)
      }
    }

  var uniformBufferObject: Int = 0
    set(value) {
      if (field != value) {
        field = value
        glBindBuffer(GL_UNIFORM_BUFFER, value)
      }
    }

  var shaderProgram: Int = 0
    set(value) {
      if (field != value) {
        field = value
        glUseProgram(value)
      }
    }

  var lineThickness: Float = 1f
    set(value) {
      if (field != value) {
        field = value
        glLineWidth(value)
      }
    }

  var pointSize: Float = 1f
    set(value) {
      if (field != value) {
        field = value
        glPointSize(value)
      }
    }

  var pointSprite: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        setEnabled(GL_POINT_SPRITE, value)
      }
    }

  var viewport: Vector4i
    set(value) {
      if (data[GlField.viewport] != value) {
        data[GlField.viewport] = value
        glViewport(value.x, value.y, value.z, value.w)
      }
    }
    get() = data[GlField.viewport] as Vector4i

  var cropBounds: Vector4i = getGLBounds(GL_SCISSOR_BOX)
    set(value) {
      if (field != value) {
        field = value
        glScissor(value.x, value.y, value.z, value.w)
      }
    }

  var cropEnabled: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        setEnabled(GL_SCISSOR_TEST, value)
      }
    }

  var blendEnabled: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        setEnabled(GL_BLEND, value)
      }
    }

  var multisampleEnabled: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        setEnabled(GL_MULTISAMPLE, value)
      }
    }

  var blendFunction: Pair<Int, Int> = Pair(glGetInteger(GL_BLEND_SRC_RGB), glGetInteger(GL_BLEND_DST_RGB))
    set(value) {
      if (field != value) {
        field = value
        glBlendFunc(value.first, value.second)
      }
    }

  var textureSlot: Int = -1
    set(value) {
      if (field != value) {
        field = value
        glActiveTexture(value)
      }
    }

  var bound2dTexture: Int = 0
    set(value) {
      if (field != value) {
        field = value
        glBindTexture(GL_TEXTURE_2D, value)
      }
    }

  var bound2dMultisampleTexture: Int = 0
    set(value) {
      if (field != value) {
        field = value
        glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, value)
      }
    }

  var depthEnabled: Boolean
    set(value) {
      depthTest = value
      depthWrite = value
    }
    get() = depthTest && depthWrite

  var depthTest: Boolean = false
    set(value) {
      if (data[GlField.depthTest] != value) {
        data[GlField.depthTest] = value
        field = value
        setEnabled(GL_DEPTH_TEST, value)
      }
    }
    get() = data[GlField.depthTest]!! as Boolean

  var depthWrite: Boolean
    set(value) {
      if (data[GlField.depthWrite] != value) {
        data[GlField.depthWrite] = value
        glDepthMask(value)
      }
    }
    get() = data[GlField.depthWrite]!! as Boolean

  var stencilTest: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        setEnabled(GL_STENCIL_TEST, value)
      }
    }

  var stencilWrite: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        val mask = if (field) 0xFF else 0x00
        glStencilMask(mask)
      }
    }

  var stenciloperation0: Int = GL_KEEP
    set(value) {
      if (field != value) {
        field = value
        glStencilOp(stenciloperation0, stenciloperation1, stenciloperation2)
      }
    }

  var stenciloperation1: Int = GL_KEEP
    set(value) {
      if (field != value) {
        field = value
        glStencilOp(stenciloperation0, stenciloperation1, stenciloperation2)
      }
    }

  var stenciloperation2: Int = GL_KEEP
    set(value) {
      if (field != value) {
        field = value
        glStencilOp(stenciloperation0, stenciloperation1, stenciloperation2)
      }
    }

  var cullFaces: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        setEnabled(GL_CULL_FACE, value)
      }
    }

  var cullFaceSides: Int = GL_BACK
    set(value) {
      if (field != value) {
        field = value
        glCullFace(value)
      }
    }

  fun setFrameBuffer(value: Int) {
    if (drawFramebuffer != value || readFramebuffer != value) {
      _drawFramebuffer = value
      _readFramebuffer = value
      glBindFramebuffer(GL_FRAMEBUFFER, value)
    }
  }

  private var _drawFramebuffer: Int = 0
  var drawFramebuffer: Int
    get() = _drawFramebuffer
    set(value) {
      if (_drawFramebuffer != value) {
        _drawFramebuffer = value
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, value)
      }
    }

  private var _readFramebuffer: Int = 0
  var readFramebuffer: Int
    get() = _readFramebuffer
    set(value) {
      val k = glGetInteger(GL_READ_FRAMEBUFFER_BINDING)
      if (_readFramebuffer != value) {
        _readFramebuffer = value
        glBindFramebuffer(GL_READ_FRAMEBUFFER, value)
      }
    }

  fun getReadFrameBuffer(): Int = _readFramebuffer
  fun getDrawFrameBuffer(): Int = _drawFramebuffer

  var vertexProgramPointSizeEnabled: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        setEnabled(GL_VERTEX_PROGRAM_POINT_SIZE, value)
      }
    }
}

val globalState = State()
