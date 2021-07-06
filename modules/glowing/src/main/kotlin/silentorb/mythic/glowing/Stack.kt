package silentorb.mythic.glowing

import silentorb.mythic.spatial.Vector4i

fun withViewport(value: Vector4i, action: () -> Unit) {
  val original = globalState.viewport
  globalState.viewport = value
  action()
  globalState.viewport = original
}

fun <T> withCropping(value: Vector4i, action: () -> T): T {
  val originalBounds = globalState.cropBounds
  val originalCropEnabled = globalState.cropEnabled
  globalState.cropBounds = value
  globalState.cropEnabled = true
  val result = action()
  globalState.cropBounds = originalBounds
  globalState.cropEnabled = originalCropEnabled
  return result
}

fun mapChanges(changes: GlStateMap): GlStateMap {
  val depthEnabled = changes[GlField.depthEnabled] as? Boolean
  val additions = if (depthEnabled != null) {
    mapOf(
        GlField.depthTest to depthEnabled,
        GlField.depthWrite to depthEnabled,
    )
  } else
    mapOf()

  return (changes - GlField.depthEnabled) + additions
}

fun withStack(changes: GlStateMap, action: () -> Unit) {
  val mappedChanges = mapChanges(changes)
  val original = mappedChanges.mapValues { change ->
    globalState.data[change.key]!!
  }
  for (change in mappedChanges) {
    setGlState(change.key, change.value)
  }

  action()

  for (setting in original) {
    setGlState(setting.key, setting.value)
  }
}
