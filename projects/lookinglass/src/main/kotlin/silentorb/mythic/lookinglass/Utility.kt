package silentorb.mythic.lookinglass

import silentorb.mythic.scenery.Shape
import silentorb.mythic.spatial.Vector4i
import silentorb.mythic.spatial.radiansToDegrees
import kotlin.math.abs
import kotlin.math.tan

// Even though numbers have no case, still include them in the regex
// so hypens or underscores followed by a number are converted
private val camelCaseRegex = Regex("[-_][a-z0-9]")

fun toCamelCase(identifier: String) =
    identifier.replace(camelCaseRegex) { it.value[1].toUpperCase().toString() }

fun getNearPlaneHeight(viewport: Vector4i, fov: Float): Float =
    abs(viewport.w - viewport.y).toFloat() / (2f * tan(0.5f * radiansToDegrees(fov)))

fun getMeshShapes(renderer: Renderer): Map<String, Shape> =
    renderer.meshes
        .filterValues { it.bounds != null }
        .mapValues { it.value.bounds!! }

fun flipY(height: Int, value: Int): Int =
    height - value

fun setElementGroupMaterial(material: Material, elementGroups: Collection<ElementGroup>) =
    elementGroups.map { group ->
      group.copy(
          meshes = group.meshes.map { mesh ->
            mesh.copy(
                material = material
            )
          }
      )
    }
