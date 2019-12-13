package silentorb.mythic.lookinglass

//class PerspectiveEffect(private val shader: GeneralPerspectiveShader, private val camera: CameraEffectsData) {
//  private val modelTransform = MatrixProperty(shader.program, "modelTransform")
//
//  initialize {
//    shader.cameraTransform.setValue(camera.transform)
//    shader.cameraDirection.setValue(camera.direction)
//  }
//
//  fun activate(transform: Matrix) {
//    modelTransform.setValue(transform)
//    shader.activate()
//  }
//}
//
//class ColoredPerspectiveEffect(val shader: GeneralPerspectiveShader,
//                               camera: CameraEffectsData,
//                               sceneBuffer: UniformBuffer) {
//  private val perspectiveEffect = PerspectiveEffect(shader.shader, camera)
//  private val sceneProperty = UniformBufferProperty(shader.shader.program, "SceneUniform")
//
//  initialize {
//    sceneProperty.setValue(sceneBuffer)
//  }
//
//  fun activate(transform: Matrix, color: Vector4, glow: Float, normalTransform: Matrix) {
//    shader.activate(color, glow, normalTransform)
//    perspectiveEffect.activate(transform)
//  }
//}
//
//class FlatColoredPerspectiveEffect(val shader: GeneralPerspectiveShader, camera: CameraEffectsData) {
//  private val perspectiveEffect = PerspectiveEffect(shader.shader, camera)
//
//  fun activate(transform: Matrix, color: Vector4) {
//    shader.activate(color)
//    perspectiveEffect.activate(transform)
//  }
//}
//
//data class TextureEffectConfig(
//    val transform: Matrix,
//    val texture: Texture,
//    val color: Vector4,
//    val glow: Float,
//    val normalTransform: Matrix
//)
//
//class TexturedPerspectiveEffect(val shader: GeneralPerspectiveShader, camera: CameraEffectsData, sceneBuffer: UniformBuffer) {
//  private val perspectiveEffect = ColoredPerspectiveEffect(shader.colorShader, camera, sceneBuffer)
//
//  fun activate(config: TextureEffectConfig) {
//    shader.activate(config.texture, config.color, config.glow, config.normalTransform)
//    perspectiveEffect.activate(config.transform, config.color, config.glow, config.normalTransform)
//  }
//}
//
////class AnimatedPerspectiveEffect(val textureEffect: TexturedPerspectiveEffect) {
////  val animatedShader = AnimatedShader(textureEffect.shader.colorShader.shader.program)
////
////  fun activate(config: TextureEffectConfig, bones: Bones) {
////    textureEffect.activate(config)
////    animatedShader.activate(bones)
////  }
////}
//
//class AnimatedFlatPerspectiveEffect(val flatEffect: FlatColoredPerspectiveEffect) {
//  val animatedShader = AnimatedShader(flatEffect.shader.shader.program)
//
//  fun activate(transform: Matrix, color: Vector4, bones: Bones) {
//    flatEffect.activate(transform, color)
//    animatedShader.activate(bones)
//  }
//}

//data class Effects(
//    val colored: ColoredPerspectiveEffect,
//    val flat: FlatColoredPerspectiveEffect,
//    val animated: AnimatedPerspectiveEffect,
//    val animatedFlat: AnimatedFlatPerspectiveEffect,
//    val textured: TexturedPerspectiveEffect,
//    val drawing: DrawingEffects
//)

//fun updateShaders(shaders: Shaders, data: EffectsData, sceneBuffer: UniformBuffer) {

//  return Effects(
//      colored = ColoredPerspectiveEffect(shaders.colored, data.camera, sceneBuffer),
//      flat = FlatColoredPerspectiveEffect(shaders.flat, data.camera),
//      textured = TexturedPerspectiveEffect(shaders.textured, data.camera, sceneBuffer),
//      animated = AnimatedPerspectiveEffect(TexturedPerspectiveEffect(shaders.animated, data.camera, sceneBuffer)),
//      animatedFlat = AnimatedFlatPerspectiveEffect(FlatColoredPerspectiveEffect(shaders.flatAnimated, data.camera)),
//      drawing = shaders.drawing
//  )
//}
