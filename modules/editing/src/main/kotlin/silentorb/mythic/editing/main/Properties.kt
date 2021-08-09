package silentorb.mythic.editing.main

import silentorb.mythic.editing.components.*
import silentorb.mythic.editing.panels.getAvailableTypes
import silentorb.mythic.ent.*
import silentorb.mythic.scenery.LightType
import silentorb.mythic.scenery.SceneProperties
import silentorb.mythic.scenery.SceneTypes
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.toList

fun commonEditorAttributes() =
    reflectProperties<String>(CommonEditorAttributes) + reflectProperties(SceneTypes)

val vector3Serialization = Serialization(
    load = {
      val value = it as List<Float>
      Vector3(value)
    },
    save = { toList(it as Vector3) }
)

val floatSerialization = Serialization(
    load = {
      val value = it as Number
      value.toFloat()
    },
    save = { it }
)

val intSerialization = Serialization(
    load = {
      val value = it as Number
      value.toInt()
    },
    save = { it }
)

inline fun <T> enumSerialization(crossinline loader: (String) -> T) = Serialization(
    load = {
      loader(it as String) as Any
    },
    save = { it }
)

val collisionGroupWidget: PropertyWidget = labeledDropDownWidget { it.enumerations.collisionPresets }

fun commonPropertyDefinitions(): PropertyDefinitions = mapOf(
    SceneProperties.mesh to PropertyDefinition(
        displayName = "Mesh",
        widget = dropDownWidget { it.enumerations.meshes },
        defaultValue = { editor -> editor.enumerations.meshes.firstOrNull() },
    ),
    SceneProperties.texture to PropertyDefinition(
        displayName = "Texture",
        widget = dropDownWidget { it.enumerations.resourceInfo.textures.keys.toList().sorted() },
        defaultValue = { editor -> editor.enumerations.resourceInfo.textures.keys.firstOrNull() },
    ),
    SceneProperties.text3d to PropertyDefinition(
        displayName = "3D Text",
        widget = propertyTextField,
        defaultValue = { editor -> getAvailableTypes(editor).firstOrNull() },
    ),
    SceneProperties.translation to PropertyDefinition(
        displayName = "Translation",
        serialization = vector3Serialization,
        widget = propertySpatialWidget,
        defaultValue = { Vector3.zero },
    ),
    SceneProperties.rotation to PropertyDefinition(
        displayName = "Rotation",
        serialization = vector3Serialization,
        widget = propertyRotationWidget,
        defaultValue = { Vector3.zero },
    ),
    SceneProperties.scale to PropertyDefinition(
        displayName = "Scale",
        serialization = vector3Serialization,
        widget = propertySpatialWidget,
        defaultValue = { Vector3.unit },
    ),
    SceneProperties.rgba to PropertyDefinition(
        displayName = "Color",
        widget = propertyRgbaField,
        defaultValue = { "#ffffffff" },
    ),
    SceneProperties.range to PropertyDefinition(
        displayName = "Range",
        serialization = floatSerialization,
        widget = propertyDecimalTextField,
        defaultValue = { 1f },
    ),
    SceneProperties.height to PropertyDefinition(
        displayName = "Height",
        serialization = floatSerialization,
        widget = propertyDecimalTextField,
        defaultValue = { 1f },
    ),
    SceneProperties.radius to PropertyDefinition(
        displayName = "Radius",
        serialization = floatSerialization,
        widget = propertyDecimalTextField,
        defaultValue = { 1f },
    ),
    SceneProperties.light to PropertyDefinition(
        displayName = "Light",
        widget = dropDownWidget { LightType.values().map { it.name } },
        dependencies = setOf(SceneProperties.rgba, SceneProperties.range),
        defaultValue = { "point" },
    ),
    SceneProperties.collisionShape to PropertyDefinition(
        displayName = "Collision Shape",
        widget = dropDownWidget { CollisionShape.values().map { it.name } },
//        dependencies = setOf(SceneProperties.collisionGroups, SceneProperties.collisionMask),
        defaultValue = { CollisionShape.box.name }
    ),
    SceneProperties.collisionGroups to PropertyDefinition(
        displayName = "Collision Group",
        widget = collisionGroupWidget,
        defaultValue = { 0 },
    ),
    SceneProperties.collisionMask to PropertyDefinition(
        displayName = "Collision Mask",
        widget = collisionGroupWidget,
        defaultValue = { 0 },
    ),
    SceneProperties.value to PropertyDefinition(
        displayName = "Value",
        serialization = intSerialization,
        widget = propertyIntegerTextField,
        defaultValue = { 0f },
    ),
    SceneProperties.connects to PropertyDefinition(
        displayName = "Connects",
        widget = propertyTextField,
        defaultValue = { "" },
    ),
)
