package silentorb.mythic.lookinglass

import silentorb.mythic.glowing.UniformBuffer
import silentorb.mythic.lookinglass.shading.LightingConfig
import silentorb.mythic.lookinglass.shading.createLightBuffer
import silentorb.mythic.scenery.Light

fun updateLights(config: LightingConfig, lights: List<Light>, uniformBuffer: UniformBuffer) {
  val byteBuffer = createLightBuffer(config, lights)
  uniformBuffer.load(byteBuffer)
}
