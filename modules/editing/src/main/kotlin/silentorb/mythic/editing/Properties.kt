package silentorb.mythic.editing

object Properties {
  const val parent = "parent"
  const val location = "location"
  const val orientation = "orientation"
  const val scale = "scale"
  const val mesh = "mesh"
}

val getSceneTree: (Graph) -> SceneTree = groupProperty(Properties.parent)
