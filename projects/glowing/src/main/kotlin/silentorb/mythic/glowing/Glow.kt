package silentorb.mythic.glowing

import silentorb.mythic.spatial.Vector4i
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL43.*

class Glow {
  val operations = Operations()
  val state: State
  val glVersion: String
  val glVendor: String
  val glRenderer: String

  init {
    GL.createCapabilities()
    glVersion = glGetString(GL_VERSION)!!
    glVendor = glGetString(GL_VENDOR)!!
    glRenderer = glGetString(GL_RENDERER)!!
    state = globalState
  }

}

fun debugMarkPass(enabled: Boolean, message: String, action: () -> Unit) {
  if (enabled) {
    glPushDebugGroup(GL_DEBUG_SOURCE_APPLICATION, 0, message)
    action()
    glPopDebugGroup()
  } else {
    action()
  }
}
