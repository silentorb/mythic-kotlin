package mythic.breeze

import mythic.spatial.*
import org.joml.times

data class ChannelTarget2(
    val bone: Bone,
    val type: ChannelType
)

enum class ChannelType {
  rotation,
  scale,
  translation
}

data class AnimationChannel2(
    val sampler: Keyframes,
    val target: ChannelTarget2
)

data class ChannelTarget(
    val boneIndex: Int,
    val type: ChannelType
)

fun areKeysValid(keys: Keyframes): Boolean =
    keys.map { it.time }.distinct().size == keys.size

data class SkeletonAnimationChannel(
    val target: ChannelTarget,
    val keys: Keyframes
) {
  init {
    assert(areKeysValid(keys))
  }
}

data class SkeletonAnimation(
    val name: String,
    val duration: Float,
    val channels: List<SkeletonAnimationChannel>,
    val channelMap: ChannelTypeMap,
    val markers: List<TimelineMarker>
)

typealias Bones = List<Bone>

typealias Transformer = (bones: Bones, bone: Bone) -> Matrix

data class Bone(
    val name: String,
    val translation: Vector3,
    val rotation: Quaternion,
    val length: Float,
    val index: Int,
    val parent: Int = -1
)

data class VertexWeight(
    val index: Int,
    val strength: Float
)

typealias VertexWeights = Pair<VertexWeight, VertexWeight>

typealias WeightMap = Map<Vector3, VertexWeights>

data class ArrangedBoneNode(
    val index: Int,
    val parent: Int
)

typealias ChannelMap = Map<Int, SkeletonAnimationChannel>
typealias ChannelTypeMap = Map<ChannelType, ChannelMap>

typealias MatrixSource = (index: Int) -> Matrix

fun mapChannels(channels: List<SkeletonAnimationChannel>): ChannelTypeMap =
    channels
        .groupBy { it.target.type }
        .mapValues { c -> c.value.associate { Pair(it.target.boneIndex, it) } }

typealias ValueSource<T> = (boneIndex: Int) -> T?

fun staticMatrixSource(bones: Bones): MatrixSource = { i ->
  val bone = bones[i]
  transformBone(bone.translation, bone.rotation)
}

fun transformSkeleton(bones: Bones, matrixSource: MatrixSource = staticMatrixSource(bones)): List<Matrix> {
  val init = Matrix()
  val result = Array(bones.size, { init })
  for (i in 0 until bones.size) {
    val bone = bones[i]
    val transform = matrixSource(i)
    result[i] = if (bone.parent == -1)
      transform
    else
      result[bone.parent] * transform
  }

  return result.toList()
}

data class IntermediateTransform(
    val translation: Vector3,
    val rotation: Quaternion
)

fun intermediateTransformAnimatedSkeleton(bones: List<Bone>, animation: MultiAnimationPart): List<Matrix> {
  val translationMap = animatedValueSource<Vector3>(animation.animation.channelMap[ChannelType.translation], animation.timeOffset)
  val rotationMap = animatedValueSource<Quaternion>(animation.animation.channelMap[ChannelType.rotation], animation.timeOffset)
  return bones.mapIndexed { i, bone ->
    val translation = (translationMap(i) ?: bone.translation) * animation.strength
    val rotation = Quaternion().slerp(rotationMap(i) ?: bone.rotation, animation.strength)
    transformBone(translation, rotation)
  }
}

fun transformAnimatedSkeleton(bones: List<Bone>, animation: SkeletonAnimation, timeElapsed: Float): List<Matrix> {
  val translationMap = animatedValueSource<Vector3>(animation.channelMap[ChannelType.translation], timeElapsed)
  val rotationMap = animatedValueSource<Quaternion>(animation.channelMap[ChannelType.rotation], timeElapsed)
  val matrixSource: MatrixSource = { i ->
    val bone = bones[i]
    val translation = translationMap(i) ?: bone.translation
    val rotation = rotationMap(i) ?: bone.rotation
    transformBone(translation, rotation)
  }
  return transformSkeleton(bones, matrixSource)
}

data class MultiAnimationPart(
    val animation: SkeletonAnimation,
    val timeOffset: Float,
    val strength: Float = 1f
)

fun transformAnimatedSkeleton(bones: List<Bone>, animations: List<MultiAnimationPart>): List<Matrix> {
  if (animations.size == 1)
    return transformAnimatedSkeleton(bones, animations.first().animation, animations.first().timeOffset)

  if (animations.size > 2)
    throw Error("Mixing any number other than two animations is not currently supported.")

  val sum = animations.map {it.strength}.sum()
  assert(sum <= 1f)
  assert(sum > 0.999f)

  val poses = animations.map { animation ->
    transformAnimatedSkeleton(bones, animation.animation, animation.timeOffset)
        .map { transform ->
          IntermediateTransform(
              translation = Vector3(transform.getTranslation(Vector3m())),
              rotation = transform.getUnnormalizedRotation(Quaternion())
          )
        }
  }

  val strengthA = animations[0].strength
  val strengthB = animations[1].strength
  val slerpRatio = strengthB / (strengthA + strengthB)
  return poses[0].zip(poses[1]) { a, b ->
    val ta = a.translation * strengthA
    val ra = a.rotation
    val tb = b.translation * strengthB
    val rb = b.rotation
    transformBone(ta + tb, ra.slerp(Quaternion(rb), slerpRatio))
  }
}

fun transformBone(translation: Vector3, rotation: Quaternion) =
    Matrix()
        .translate(translation)
        .rotate(rotation)

fun <T> emptyValueSource(): ValueSource<T> = { null }

fun <T> animatedValueSource(channelMap: ChannelMap?, timePassed: Float): ValueSource<T> =
    if (channelMap == null)
      emptyValueSource()
    else
      { i ->
        val channel = channelMap[i]
        if (channel != null)
          getStandardChannelValue(channel.keys, timePassed) as T
        else
          null
      }

fun projectBoneTail(matrix: Matrix, bone: Bone) =
//    Matrix().translate(Vector3(bone.length, 0f, 0f)).mul(matrix)
    Matrix(matrix).translate(bone.length, 0f, 0f)

fun getBoneIndex(bones: Bones, name: String): Int =
    bones.first { it.name == name }.index

fun getBone(bones: Bones, name: String): Bone =
    bones.first { it.name == name }

fun refineKeyframes(keys: Keyframes): Keyframes =
    if (keys.first().time == 0f)
      keys
    else
      listOf(keys.first().copy(time = 0f))
          .plus(keys)

fun shift(timeOffset: Float, duration: Float, keys: Keyframes): Keyframes =
    if (timeOffset == 0f)
      keys
    else {
      val (first, second) = keys.filter { it.time < duration }.partition { it.time + timeOffset > duration }
      val result = first.map { it.copy(time = it.time + timeOffset - duration) }
          .plus(second.map { it.copy(time = it.time + timeOffset) })
      listOf(result.last().copy(time = 0f)).plus(result)
    }

fun keySequence(offset: Vector3, increment: Float, values: List<Vector3>): Keyframes =
    values.mapIndexed { index, value ->
      Keyframe(increment * index, offset + value)
    }

fun keySequenceRotation(offset: Quaternion, increment: Float, values: List<Quaternion>): Keyframes =
    values.mapIndexed { index, value ->
      Keyframe(increment * index, offset * value)
    }
