package silentorb.mythic.scenery

import silentorb.mythic.ent.PropertyInfo

object SceneProperties {
  const val parent = "parent"
  const val translation = "translation"
  const val rotation = "rotation"
  const val scale = "scale"
  const val mesh = "mesh"
  const val texture = "texture"
  const val instance = "instance"
  const val text3d = "text3d"
  const val light = "light"
  const val attribute = "attribute"
  const val rgba = "rgba"
  const val range = "range"
  const val collisionShape = "collisionShape"
  const val collisionGroups = "collisionGroups"
  const val collisionMask = "collisionMask"
}

val scenePropertiesInfo = mapOf(
    SceneProperties.attribute to PropertyInfo(
        manyToMany = true,
        type = String,
    )
)
