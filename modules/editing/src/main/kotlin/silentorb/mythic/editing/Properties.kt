package silentorb.mythic.editing

import silentorb.mythic.editing.components.*
import silentorb.mythic.editing.panels.getAvailableTypes
import silentorb.mythic.ent.*
import silentorb.mythic.scenery.LightType
import silentorb.mythic.scenery.Properties
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.toList

fun commonEditorAttributes() =
    reflectProperties<String>(CommonEditorAttributes)

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

fun commonPropertyDefinitions(): PropertyDefinitions = mapOf(
    Properties.mesh to PropertyDefinition(
        displayName = "Mesh",
        widget = dropDownWidget { it.enumerations.meshes },
        defaultValue = { editor -> editor.enumerations.meshes.firstOrNull() },
    ),
    Properties.texture to PropertyDefinition(
        displayName = "Texture",
        widget = dropDownWidget { it.enumerations.textures },
        dependencies = setOf(Properties.mesh),
        defaultValue = { editor -> editor.enumerations.textures.firstOrNull() },
    ),
    Properties.instance to PropertyDefinition(
        displayName = "Type",
        widget = dropDownWidget(::getAvailableTypes),
        defaultValue = { editor -> getAvailableTypes(editor).firstOrNull() },
    ),
    Properties.text3d to PropertyDefinition(
        displayName = "3D Text",
        widget = propertyTextField,
        defaultValue = { editor -> getAvailableTypes(editor).firstOrNull() },
    ),
    Properties.translation to PropertyDefinition(
        displayName = "Translation",
        serialization = vector3Serialization,
        widget = propertySpatialWidget,
        defaultValue = { Vector3.zero },
    ),
    Properties.rotation to PropertyDefinition(
        displayName = "Rotation",
        serialization = vector3Serialization,
        widget = propertySpatialWidget,
        defaultValue = { Vector3.zero },
    ),
    Properties.scale to PropertyDefinition(
        displayName = "Scale",
        serialization = vector3Serialization,
        widget = propertySpatialWidget,
        defaultValue = { Vector3.unit },
    ),
    Properties.rgba to PropertyDefinition(
        displayName = "Color",
        widget = propertyRgbaField,
        defaultValue = { "#ffffffff" },
    ),
    Properties.range to PropertyDefinition(
        displayName = "Range",
        serialization = floatSerialization,
        widget = propertyDecimalTextField,
        defaultValue = { 1f },
    ),
    Properties.light to PropertyDefinition(
        displayName = "Light",
        widget = dropDownWidget { LightType.values().map { it.name } },
        dependencies = setOf(Properties.rgba, Properties.range),
        defaultValue = { "point" },
    ),
    Properties.collisionShape to PropertyDefinition(
        displayName = "Collision Shape",
        widget = dropDownWidget { CollisionShape.values().map { it.name } },
        defaultValue = { CollisionShape.box.name }
    ),
    Properties.collisionGroups to PropertyDefinition(
        displayName = "Collision Group",
        widget = propertyBitmaskField,
        defaultValue = { 0 },
    ),
    Properties.collisionMask to PropertyDefinition(
        displayName = "Collision Mask",
        widget = propertyBitmaskField,
        defaultValue = { 0 },
    ),
)
