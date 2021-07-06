package silentorb.mythic.lookinglass.meshes.loading

import silentorb.mythic.breeze.*
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.lookinglass.AnimationMap
import silentorb.mythic.lookinglass.Armature
import silentorb.mythic.lookinglass.SocketMap
import silentorb.mythic.lookinglass.toCamelCase
import java.nio.ByteBuffer

typealias SkinMap = Map<Int, Int>

fun mapSkinIndices(info: GltfInfo, node: Node, boneMap: Map<Int, BoneNode>): SkinMap {
  val skin = info.skins!![node.skin!!]
  return skin.joints.mapIndexed { index, jointIndex ->
    Pair(index, boneMap[jointIndex]!!.index)
  }
      .associate { it }
}

fun formatArmatureName(name: String): String =
    toCamelCase(name.replace("rig_", ""))

fun getParentBone(info: GltfInfo, nodeIndex: Int, boneMap: BoneMap): Int? {
  val node = info.nodes[nodeIndex]
  return if (node.extras != null && node.extras.containsKey("parent")) {
    val rawName = node.extras["parent"] as String
    val parentName = rawName.replace("rig_", "")
    boneMap.values.firstOrNull { it.name == parentName }?.index
  } else
    null
}

fun convertChannelType(source: String): ChannelType =
    when (source) {
      "rotation" -> ChannelType.rotation
      "scale" -> ChannelType.scale
      "translation" -> ChannelType.translation
      else -> throw Error("Unsupported channel type: $source")
    }

fun loadChannel(target: ChannelTarget, buffer: ByteBuffer, info: GltfInfo, sampler: AnimationSampler, boneIndex: BoneNode): SkeletonAnimationChannel {
  val inputAccessor = info.accessors[sampler.input]
  val inputBufferView = info.bufferViews[inputAccessor.bufferView]
  val outputAccessor = info.accessors[sampler.output]
  val outputBufferView = info.bufferViews[outputAccessor.bufferView]
  val times = getFloats(buffer, inputBufferView.byteOffset, inputAccessor.count)
  val values: List<Any> = when (target.path) {
    "translation" -> getVector3List(buffer, outputBufferView.byteOffset, outputAccessor.count)
    "rotation" -> getQuaternions(buffer, outputBufferView.byteOffset, outputAccessor.count)
    "scale" -> getVector3List(buffer, outputBufferView.byteOffset, outputAccessor.count)
    else -> throw Error("Not implemented.")
  }

  return SkeletonAnimationChannel(
      target = ChannelTarget(boneIndex.index, convertChannelType(target.path)),
      keys = times.zip(values) { time, value -> Keyframe(time, value) }
  )
}

fun loadAnimation(buffer: ByteBuffer, info: GltfInfo, source: IndexedAnimation, boneIndexMap: Map<Int, BoneNode>): SkeletonAnimation {
  var duration = 0f
  val n = source.channels.map { info.nodes[it.target.node] }
  val channels = source.channels
      .filter { it.target.path != "scale" }
      .mapNotNull {
        val boneIndex = boneIndexMap[it.target.node]
        if (boneIndex == null)
          null
        else {
          val channel = loadChannel(it.target, buffer, info, source.samplers[it.sampler], boneIndex)

          val lastTime = channel.keys.last().time
          if (lastTime > duration)
            duration = lastTime

          channel
        }
      }

  return SkeletonAnimation(
      channels = channels,
      channelMap = mapChannels(channels),
      duration = duration,
      markers = source.extras?.markers ?: listOf()
  )
}

fun loadAnimations(buffer: ByteBuffer, info: GltfInfo, animations: List<IndexedAnimation>,
                   bones: List<Bone>, boneIndexMap: Map<Int, BoneNode>): AnimationMap {
  return animations.map { source ->
    val name = formatArmatureName(source.name)
    Pair(name, loadAnimation(buffer, info, source, boneIndexMap))
  }
      .associate { it }
}

fun nodeToBone(node: Node, index: Int, parent: Int) =
    Bone(
        name = node.name,
        translation = node.translation ?: Vector3.zero,
        rotation = loadQuaternion(node.rotation),
        length = 0.1f,
        index = index,
        parent = parent
    )

data class InitialBoneNode(
    val name: String,
    val originalIndex: Int,
    val level: Int,
    val parent: Int
)

data class BoneNode(
    val name: String,
    val index: Int,
    val originalIndex: Int,
    val parent: Int,
    val originalParent: Int
)

typealias BoneMap = Map<Int, BoneNode>

fun gatherBoneHierarchy(nodes: List<Node>, root: Int, level: Int = 0, parent: Int = -1): List<InitialBoneNode> {
  val node = nodes[root]
  val children = node.children
  val descendents: List<InitialBoneNode> = children?.flatMap { child ->
    gatherBoneHierarchy(nodes, child, level + 1, root).toList()
  } ?: listOf()

  return listOf(InitialBoneNode(
      name = node.name,
      originalIndex = root,
      level = level,
      parent = parent
  ))
      .plus(descendents)
}

fun orderBoneHierarchy(levelMap: List<InitialBoneNode>): List<InitialBoneNode> {
  val top = levelMap.maxBy { it.level }!!.level
  return (0..top).flatMap { level ->
    levelMap.filter { it.level == level }
  }
}

fun getSockets(nodes: List<Node>): SocketMap =
    nodes.mapIndexedNotNull { index, node ->
      val socket = node.extras?.get("socket")
      if (socket is String)
        Pair(socket, index)
      else
        null
    }
        .associate { it }

fun getAncestors(nodes: List<Node>, bone: Int): List<Int> {
  val parent = nodes.indexOfFirst { it.children != null && it.children.contains(bone) }
  return if (parent == -1)
    listOf()
  else
    listOf(parent).plus(getAncestors(nodes, parent))
}

fun getSkeletonRoot(info: GltfInfo): Int {
  val nodes = info.nodes
  val root = nodes.indexOfFirst { it.name == "root" }
  if (root == -1) {
    // For debug purposes
    val secondRoot = nodes.filterIndexed { index, node ->
      nodes.none {
        it.children?.contains(index) ?: false
      }
    }
    throw Error("Could not find skeleton root for model")
  }
  return root
}

fun getBoneMap(info: GltfInfo, additionalBones: Collection<Int>): BoneMap {
  val skins = info.skins
  if (skins == null)
    return mapOf()

  val deformingBones = skins
      .flatMap { skin -> skin.joints }
      .distinct()

  val ancestors = deformingBones
      .flatMap { getAncestors(info.nodes, it) }
      .distinct()

  val deformingBonesAndAncestors = deformingBones
      .plus(ancestors)
      .plus(additionalBones)
      .distinct()

  val root = getSkeletonRoot(info)
  val levelMap = gatherBoneHierarchy(info.nodes, root)
  val orderMap = orderBoneHierarchy(levelMap)
      .filter {
        deformingBonesAndAncestors.contains(it.originalIndex)
      }

  val result = orderMap
      .mapIndexed { i,
                    item ->
        Pair(item.originalIndex, BoneNode(
            name = item.name,
            index = i,
            originalIndex = item.originalIndex,
            parent = if (item.parent == -1) -1 else orderMap.indexOfFirst { it.originalIndex == item.parent },
            originalParent = item.parent
        ))
      }
      .associate { it }

  return result
}

fun loadArmature(buffer: ByteBuffer, info: GltfInfo, filename: String, boneMap: BoneMap, socketMap: SocketMap): Armature? {
  if (info.animations == null || info.animations.none())
    return null
  val bones = boneMap.map { (_, item) ->
    val node = info.nodes[item.originalIndex]
    nodeToBone(node, item.index, item.parent)
  }

  return Armature(
      id = toCamelCase(filename),
      bones = bones,
      animations = loadAnimations(buffer, info, info.animations, bones, boneMap),
      transforms = transformSkeleton(bones),
      sockets = socketMap
  )
}
