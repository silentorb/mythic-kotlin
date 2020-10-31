package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString
import silentorb.mythic.editing.Entry
import silentorb.mythic.editing.Option
import silentorb.mythic.spatial.Vector3

fun dropDownWidget(options: List<Option>, entry: Entry): String {
  val value = entry.target as String
  var nextValue = value
  ImGui.pushID(entry.property)
  if (ImGui.beginCombo("", value)) {
    for (option in options) {
      if (ImGui.selectable(option.value)) {
        nextValue = option.value
      }
    }
    ImGui.endCombo()
  }
  ImGui.popID()
  return nextValue
}

val activeInputTextValue = ImString()
val inputTextValue = ImString()
var activeFieldId: String? = null

fun axisInput(owner: String, label: String, value: Float): Float {
  val fieldId = "$owner.$label"
  val valueReference = if (fieldId == activeFieldId)
    activeInputTextValue
  else {
    inputTextValue.set(value.toString())
    inputTextValue
  }

  ImGui.text(label)
  ImGui.sameLine()
  ImGui.setNextItemWidth(80f)
  ImGui.pushID(fieldId)
  ImGui.inputText("", valueReference, ImGuiInputTextFlags.CharsDecimal or ImGuiInputTextFlags.AutoSelectAll)
  if (ImGui.isItemActive()) {
    if (activeFieldId != fieldId) {
      activeFieldId = fieldId
      activeInputTextValue.set(valueReference.get())
    }
  }
  else if (activeFieldId == fieldId) {
    activeFieldId = null
  }
  ImGui.popID()
  return valueReference.get().toFloatOrNull() ?: value
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
