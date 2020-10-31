package silentorb.mythic.editing

import silentorb.mythic.spatial.Vector3

object Properties {
  const val parent = "parent"
  const val translation = "translation"
  const val rotation = "rotation"
  const val scale = "scale"
  const val mesh = "mesh"
  const val texture = "texture"
}

val getSceneTree: (Graph) -> SceneTree = groupProperty(Properties.parent)

object Widgets {
  const val meshSelect = "meshSelect"
  const val textureSelect = "textureSelect"
  const val translation = "translation"
  const val rotation = "rotation"
  const val scale = "scale"
}

fun commonPropertyDefinitions(): PropertyDefinitions = mapOf(
    Properties.mesh to PropertyDefinition(
        displayName = "Mesh",
        widget = Widgets.meshSelect,
        defaultValue = { editor -> editor.meshes.firstOrNull()?.value }
    ),
    Properties.texture to PropertyDefinition(
        displayName = "Texture",
        widget = Widgets.textureSelect,
        dependencies = setOf(Properties.mesh),
        defaultValue = { editor -> editor.textures.firstOrNull()?.value }
    ),
    Properties.translation to PropertyDefinition(
        displayName = "Translation",
        widget = Widgets.translation,
        defaultValue = { Vector3.zero }
    ),
    Properties.rotation to PropertyDefinition(
        displayName = "Rotation",
        widget = Widgets.rotation,
        defaultValue = { Vector3.zero }
    ),
    Properties.scale to PropertyDefinition(
        displayName = "Scale",
        widget = Widgets.scale,
        defaultValue = { Vector3.unit }
    ),
)
