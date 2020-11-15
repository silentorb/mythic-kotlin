package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString
import silentorb.mythic.editing.*
import silentorb.mythic.ent.Entry
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.scenery.arrayToHexColorString
import silentorb.mythic.ent.scenery.hexColorStringToVector4
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.degreesToRadians
import silentorb.mythic.spatial.radiansToDegrees
import silentorb.mythic.spatial.toList

fun dropDownWidget(options: List<Key>, entry: Entry): String {
  val value = entry.target as String
  var nextValue = value
  ImGui.pushID(entry.property)
  if (ImGui.beginCombo("", value)) {
    for (option in options) {
      if (ImGui.selectable(option)) {
        nextValue = option
      }
    }
    if (nextValue == value) {
      activeInputType = InputType.dropdown
    }
    ImGui.endCombo()
  }
  ImGui.popID()
  return nextValue
}

typealias EditorOptionsSource = (Editor) -> List<Key>

fun dropDownWidget(options: EditorOptionsSource): PropertyWidget = { editor, entry ->
  dropDownWidget(options(editor), entry)
}

val activeInputTextValue = ImString()
val inputTextValue = ImString()
var activeFieldId: String? = null

fun getTextValueReference(fieldId: String, value: String) =
    if (fieldId == activeFieldId)
      activeInputTextValue
    else {
      inputTextValue.set(value)
      inputTextValue
    }

fun textField(fieldId: String, value: String, flags: Int = ImGuiInputTextFlags.None): String {
  val valueReference = getTextValueReference(fieldId, value)
  ImGui.pushID(fieldId)
  ImGui.inputText("", valueReference, flags)
  checkActiveInputType(InputType.text)

  if (ImGui.isItemActive()) {
    if (activeFieldId != fieldId) {
      activeFieldId = fieldId
      activeInputTextValue.set(valueReference.get())
    }
  } else if (activeFieldId == fieldId) {
    activeFieldId = null
  }
  ImGui.popID()
  return valueReference.get()
}

fun rgbaField(entry: Entry): String {
  val value = entry.target as String
  val owner = "${entry.source}.${entry.property}"
  val reference = toList(hexColorStringToVector4(value)).toFloatArray()
  if (ImGui.colorEdit3("##$owner", reference)) {
    val k = 0
  }
  return arrayToHexColorString(reference)
}

val propertyRgbaField: PropertyWidget = { _, entry -> rgbaField(entry) }

fun textField(entry: Entry): String {
  val value = entry.target as String
  val owner = "${entry.source}.${entry.property}"
  return textField(owner, value)
}

val propertyTextField: PropertyWidget = { _, entry -> textField(entry) }

fun decimalTextField(entry: Entry): Float {
  val value = entry.target as Float
  val owner = "${entry.source}.${entry.property}"
  val result = textField(owner, value.toString(), ImGuiInputTextFlags.CharsDecimal)
  return result.toFloatOrNull() ?: value
}

val propertyDecimalTextField: PropertyWidget = { _, entry -> decimalTextField(entry) }

fun axisInput(owner: String, label: String, value: Float): Float {
  val flags = ImGuiInputTextFlags.CharsDecimal or ImGuiInputTextFlags.AutoSelectAll
  ImGui.text(label)
  ImGui.sameLine()
  val fieldId = "$owner.$label"
  ImGui.setNextItemWidth(80f)
  val result = textField(fieldId, value.toString(), flags)
  return result.toFloatOrNull() ?: value
}

fun spatialWidget(entry: Entry): Vector3 {
  val value = entry.target as Vector3
  val owner = "${entry.source}.${entry.property}"
  val x = axisInput(owner, "X", value.x)
  ImGui.sameLine()
  val y = axisInput(owner, "Y", value.y)
  ImGui.sameLine()
  val z = axisInput(owner, "Z", value.z)
  return Vector3(x, y, z)
//  return Vector3(x, 0f, 0f)
}

val propertySpatialWidget: PropertyWidget = { _, entry -> spatialWidget(entry) }

fun rotationWidget(entry: Entry): Vector3 {
  val result = spatialWidget(entry.copy(target = radiansToDegrees(entry.target as Vector3)))
  return degreesToRadians(result)
}

val propertyRotationWidget: PropertyWidget = { _, entry -> rotationWidget(entry) }

fun bitmaskField(entry: Entry): Int {
  return 0
}

val propertyBitmaskField: PropertyWidget = { _, entry -> bitmaskField(entry) }
