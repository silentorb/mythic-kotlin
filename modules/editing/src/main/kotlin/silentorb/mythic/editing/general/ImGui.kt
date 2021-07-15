package silentorb.mythic.editing.general

import imgui.ImFontConfig
import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import silentorb.mythic.haft.InputDeviceState
import silentorb.mythic.happenings.Commands

private var imguiInitialized: Boolean = false
private var imGuiGlfw: ImGuiImplGlfw? = null
private var imGuiGl3: ImGuiImplGl3? = null
private var renderReady: Boolean = false

enum class InputType {
  dropdown,
  text,
}

var activeInputType: InputType? = null

fun checkActiveInputType(type: InputType) {
  if (ImGui.isItemActive()) {
    activeInputType = type
  }
}

fun isImGuiFieldActive(): Boolean =
    activeInputType != null

fun initializeImGui(fonts: List<Typeface>, window: Long) {
  imguiInitialized = true
  ImGui.createContext()
  val io = ImGui.getIO()
  io.iniFilename = null
  io.addConfigFlags(ImGuiConfigFlags.DockingEnable)
//  io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard)
//  io.addConfigFlags(ImGuiConfigFlags.NavEnableGamepad)
  io.configDockingTransparentPayload = true
  io.configViewportsNoAutoMerge = true
  io.configViewportsNoTaskBarIcon = true
  io.iniFilename = "editor.ini"

  val fontAtlas = io.fonts
  val fontConfig = ImFontConfig()
  fontConfig.glyphRanges = fontAtlas.glyphRangesCyrillic
  fontAtlas.addFontDefault()
  for (font in fonts) {
    fontConfig.setName("${font.name}, ${font.size}px")
    fontAtlas.addFontFromMemoryTTF(loadFromResources(font.path), font.size, fontConfig)
  }
  fontConfig.destroy()

  imGuiGlfw = ImGuiImplGlfw()
  imGuiGlfw!!.init(window, true)
  imGuiGl3 = ImGuiImplGl3()
  imGuiGl3!!.init()
}

fun closeImGui() {
  imGuiGl3?.dispose()
  imGuiGlfw?.dispose()
  if (imguiInitialized) {
    ImGui.destroyContext()
  }
}

fun ensureImGuiIsInitialized(fonts: List<Typeface>, window: Long) {
  if (!imguiInitialized) {
    initializeImGui(fonts, window)
  }
}

fun prepareEditorDefinition(deviceStates: List<InputDeviceState>): Boolean {
  if (!imguiInitialized)
    return false

  if (renderReady) {
    ImGui.endFrame()
  }

  renderReady = true
  imGuiGlfw!!.newFrame()
  ImGui.newFrame()
  updateModifierKeyStates(deviceStates.lastOrNull()?.events ?: listOf())
  activeInputType = null

  return true
}

fun renderEditorGui() {
  if (!imguiInitialized)
    return

  if (renderReady) {
    ImGui.render()
    renderReady = false
  }
  imGuiGl3!!.renderDrawData(ImGui.getDrawData())
}
