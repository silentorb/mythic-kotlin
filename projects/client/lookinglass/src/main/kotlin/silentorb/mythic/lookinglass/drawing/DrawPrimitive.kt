package silentorb.mythic.lookinglass.drawing

import silentorb.mythic.glowing.DrawMethod
import silentorb.mythic.glowing.drawMesh
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.lookinglass.Renderer
import silentorb.mythic.lookinglass.meshes.Primitive
import silentorb.mythic.lookinglass.shading.ObjectShaderConfig
import silentorb.mythic.lookinglass.shading.ShaderFeatureConfig

fun drawPrimitive(renderer: Renderer, primitive: Primitive, transform: Matrix, color: Vector4? = null){
  val material = primitive.material
  val texture = renderer.textures[material.texture]
  val effect = renderer.getShader(primitive.mesh.vertexSchema, ShaderFeatureConfig(
      texture = texture != null
  ))
  effect.activate(ObjectShaderConfig(
      transform,
      color = color ?: material.color,
      glow = material.glow,
      normalTransform = Matrix.identity,
      texture = texture
  ))
  drawMesh(primitive.mesh, DrawMethod.triangleFan)
}
