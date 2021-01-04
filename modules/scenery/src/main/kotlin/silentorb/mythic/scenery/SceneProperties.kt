package silentorb.mythic.scenery

import silentorb.mythic.ent.PropertyInfo
import silentorb.mythic.ent.PropertySchema

object SceneProperties {
  const val parent = "parent"
  const val translation = "translation"
  const val rotation = "rotation"
  const val scale = "scale"
  const val mesh = "mesh"
  const val texture = "texture"
  const val type = "type"
  const val text3d = "text3d"
  const val light = "light"
  const val rgba = "rgba"
  const val range = "range"
  const val collisionShape = "collisionShape"
  const val collisionGroups = "collisionGroups"
  const val collisionMask = "collisionMask"
  const val value = "value"
  const val connects = "connects"
}

fun scenePropertiesSchema(): PropertySchema = mapOf(
    SceneProperties.connects to PropertyInfo(
        manyToMany = true,
    ),
    SceneProperties.type to PropertyInfo(
        manyToMany = true,
    )
)
