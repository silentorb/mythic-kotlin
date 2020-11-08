package silentorb.mythic.editing

import silentorb.mythic.editing.panels.getAvailableTypes
import silentorb.mythic.ent.reflectProperties
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.toList

object Properties {
  const val parent = "parent"
  const val translation = "translation"
  const val rotation = "rotation"
  const val scale = "scale"
  const val mesh = "mesh"
  const val texture = "texture"
  const val type = "type"
  const val text3d = "text3d"
  const val attribute = "attribute"
}

val getSceneTree: (Graph) -> SceneTree = groupProperty(Properties.parent)

object Widgets {
  const val meshSelect = "meshSelect"
  const val textureSelect = "textureSelect"
  const val typeSelect = "typeSelect"
  const val translation = "translation"
  const val rotation = "rotation"
  const val scale = "scale"
  const val text = "text"
}

object Attributes {
  val editorOnly = "editorOnly"
}

fun getCommonEditorAttributes() =
    reflectProperties<String>(Attributes)

val vector3Serialization = Serialization(
    load = {
      val value = it as List<Float>
      Vector3(value)
    },
    save = { toList(it as Vector3) }
)

fun commonPropertyDefinitions(): PropertyDefinitions = mapOf(
    Properties.mesh to PropertyDefinition(
        displayName = "Mesh",
        widget = Widgets.meshSelect,
        defaultValue = { editor -> editor.meshes.firstOrNull() }
    ),
    Properties.texture to PropertyDefinition(
        displayName = "Texture",
        widget = Widgets.textureSelect,
        dependencies = setOf(Properties.mesh),
        defaultValue = { editor -> editor.textures.firstOrNull() }
    ),
    Properties.type to PropertyDefinition(
        displayName = "Type",
        widget = Widgets.typeSelect,
        defaultValue = { editor -> getAvailableTypes(editor).firstOrNull() }
    ),
    Properties.text3d to PropertyDefinition(
        displayName = "3D Text",
        widget = Widgets.text,
        defaultValue = { editor -> getAvailableTypes(editor).firstOrNull() }
    ),
    Properties.translation to PropertyDefinition(
        displayName = "Translation",
        widget = Widgets.translation,
        defaultValue = { Vector3.zero },
        serialization = vector3Serialization,
    ),
    Properties.rotation to PropertyDefinition(
        displayName = "Rotation",
        widget = Widgets.rotation,
        defaultValue = { Vector3.zero },
        serialization = vector3Serialization,
    ),
    Properties.scale to PropertyDefinition(
        displayName = "Scale",
        widget = Widgets.scale,
        defaultValue = { Vector3.unit },
        serialization = vector3Serialization,
    ),
)
