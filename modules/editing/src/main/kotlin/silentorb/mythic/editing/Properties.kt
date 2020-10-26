package silentorb.mythic.editing

object Properties {
  const val parent = "parent"
  const val location = "location"
  const val orientation = "orientation"
  const val scale = "scale"
  const val mesh = "mesh"
  const val texture = "texture"
}

val getSceneTree: (Graph) -> SceneTree = groupProperty(Properties.parent)

fun commonPropertyDefinitions(): PropertyDefinitions = mapOf(
    Properties.mesh to PropertyDefinition(
        displayName = "Mesh",
        widget = "meshSelect",
    ),
    Properties.mesh to PropertyDefinition(
        displayName = "Texture",
        widget = "textureSelect",
        dependencies = setOf(Properties.mesh)
    ),
)
