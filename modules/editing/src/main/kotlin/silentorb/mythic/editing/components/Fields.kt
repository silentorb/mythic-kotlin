package silentorb.mythic.editing.components

import imgui.ImGui
import imgui.flag.ImGuiInputTextFlags
import imgui.type.ImString
import silentorb.mythic.editing.*
import silentorb.mythic.editing.general.InputType
import silentorb.mythic.editing.general.activeInputType
import silentorb.mythic.editing.general.checkActiveInputType
import silentorb.mythic.ent.Entry
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.scenery.arrayToHexColorString
import silentorb.mythic.ent.scenery.hexColorStringToVector4
import silentorb.mythic.spatial.Vector3
import silentorb.mythic.spatial.degreesToRadians
import silentorb.mythic.spatial.radiansToDegrees
import silentorb.mythic.spatial.toList

var _stagingValue: Any? = null
var _stagingValueKey: String = ""

inline fun <reified T> stagingValue(key: String, default: () -> T): T =
    if (key == _stagingValueKey)
      _stagingValue as? T ?: default()
    else {
      _stagingValueKey = key
      val value = default()
      _stagingValue = value
      value
    }

fun <T> wrapSimpleWidget(widget: (Entry) -> T): PropertyWidget = { _, entry, _ -> widget(entry) }

fun dropDownWidget(options: List<Key>, id: String, value: String?): String? {
  var nextValue = value
  ImGui.pushID(id)
  if (ImGui.beginCombo("", value ?: "")) {
    for (option in options.sorted()) {
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

fun dropDownWidget(options: List<Key>, entry: Entry): String? =
    dropDownWidget(options, entry.property, entry.target as? String)

fun <T> labeledDropDownWidget(options: Map<T, String>, entry: Entry): T {
  val value = entry.target as T
  var nextValue = value
  ImGui.pushID(entry.property)
  val label = options[value] ?: "[Unknown]"
  if (ImGui.beginCombo("", label)) {
    for (option in options.entries.sortedBy { it.value }) {
      if (ImGui.selectable(option.value)) {
        nextValue = option.key
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
typealias LabeledEditorOptionsSource<T> = (Editor) -> Map<T, Key>

fun dropDownWidget(options: EditorOptionsSource): PropertyWidget = { editor, entry, _ ->
  dropDownWidget(options(editor), entry)
}

fun <T> labeledDropDownWidget(options: LabeledEditorOptionsSource<T>): PropertyWidget = { editor, entry, _ ->
  labeledDropDownWidget(options(editor), entry) as Any
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
  val result = arrayToHexColorString(reference)
  // Working around a bug with Dear ImGui where we are sometimes getting truncated color strings
  return if (result.length == 9)
    result
  else
    value
}

val propertyRgbaField: PropertyWidget = wrapSimpleWidget(::rgbaField)

fun textField(entry: Entry, id: String): String {
  val value = entry.target as String
  return textField(id, value)
}

val propertyTextField: PropertyWidget = { _, entry, id -> textField(entry, id) }

fun decimalTextField(entry: Entry): Float {
  val value = entry.target as Float
  val owner = "${entry.source}.${entry.property}"
  val result = textField(owner, value.toString(), ImGuiInputTextFlags.CharsDecimal)
  return result.toFloatOrNull() ?: value
}

fun integerTextField(id: String, value: Int): Int {
  val result = textField(id, value.toString(), ImGuiInputTextFlags.CharsDecimal)
  return result.toIntOrNull() ?: value
}

fun integerTextField(entry: Entry): Int =
    integerTextField("${entry.source}.${entry.property}", entry.target as Int)

val propertyDecimalTextField: PropertyWidget = wrapSimpleWidget(::decimalTextField)
val propertyIntegerTextField: PropertyWidget = wrapSimpleWidget(::integerTextField)

fun axisInput(owner: String, label: String, value: Float): Float {
  val flags = ImGuiInputTextFlags.CharsDecimal or ImGuiInputTextFlags.AutoSelectAll
  ImGui.text(label)
  ImGui.sameLine()
  val fieldId = "$owner.$label"
  ImGui.setNextItemWidth(80f)
  val result = textField(fieldId, value.toString(), flags)
  return result.toFloatOrNull() ?: value
}

fun spatialWidget(entry: Entry): Vector3? {
  val owner = "${entry.source}.${entry.property}"
  val value = entry.target as? Vector3 ?: stagingValue(owner) { Vector3.zero }
  val x = axisInput(owner, "X", value.x)
  ImGui.sameLine()
  val y = axisInput(owner, "Y", value.y)
  ImGui.sameLine()
  val z = axisInput(owner, "Z", value.z)
  return if (x == value.x && y == value.y && z == value.z)
    null
  else
    Vector3(x, y, z)
}

val propertySpatialWidget: PropertyWidget = wrapSimpleWidget(::spatialWidget)

fun rotationWidget(entry: Entry): Vector3? {
  val original = entry.target as? Vector3 ?: stagingValue("${entry.source}.${entry.property}") { Vector3.zero }
  val degreesValue = radiansToDegrees(original)
  val result = spatialWidget(entry.copy(target = degreesValue))
  return if (result == null)
    null
  else if (degreesValue == result)
    original
  else
    degreesToRadians(result)
}

val propertyRotationWidget: PropertyWidget = wrapSimpleWidget(::rotationWidget)

fun bitmaskField(entry: Entry): Int {
  return 0
}

val propertyBitmaskField: PropertyWidget = wrapSimpleWidget(::bitmaskField)
