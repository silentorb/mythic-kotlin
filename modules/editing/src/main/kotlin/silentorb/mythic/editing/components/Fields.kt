package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString
import silentorb.mythic.editing.*
import silentorb.mythic.spatial.Vector3

fun dropDownWidget(options: List<Id>, entry: Entry): String {
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

fun textField(label: String, entry: Entry): String {
  val value = entry.target as String
  val owner = "${entry.source}.${entry.property}"
  ImGui.text(label)
  ImGui.sameLine()
  val fieldId = "$owner.$label"
  return textField(fieldId, value)
}

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
