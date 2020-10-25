package silentorb.mythic.lookinglass

import silentorb.mythic.breeze.AnimationName
import silentorb.mythic.scenery.MeshName
import silentorb.mythic.scenery.TextureName
import silentorb.mythic.spatial.Matrix
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.Vector4
import silentorb.mythic.scenery.ArmatureName
import silentorb.mythic.scenery.Light
import silentorb.mythic.typography.IndexedTextStyle

data class ElementAnimation(
    val animationId: AnimationName,
    val timeOffset: Float,
    val strength: Float
)

data class MeshElement(
    val id: Long = 1L,
    val mesh: MeshName,
    val transform: Matrix = Matrix.identity,
    val location: Vector3 = Vector3.zero,
    val material: Material? = null
)

data class AttachedMesh(
    val socket: String,
    val mesh: MeshElement
)

data class TexturedBillboard(
    val texture: TextureName,
    val position: Vector3,
    val color: Vector4,
    val scale: Float,
    val step: Int = 0
)

data class TextBillboard(
    val content: String,
    val position: Vector3,
    val style: IndexedTextStyle,
    val depthOffset: Float = 0f
)

data class ElementGroup(
    val meshes: List<MeshElement> = listOf(),
    val armature: ArmatureName? = null,
    val animations: List<ElementAnimation> = listOf(),
    val attachments: List<AttachedMesh> = listOf(),
    val billboards: List<TexturedBillboard> = listOf(),
    val lights: List<Light> = listOf(),
    val textBillboards: List<TextBillboard> = listOf()
)

typealias ElementGroups = List<ElementGroup>
