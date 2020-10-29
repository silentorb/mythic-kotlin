package silentorb.mythic.editing

import imgui.ImFontConfig
import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw

private var imguiInitialized: Boolean = false
private var imGuiGlfw: ImGuiImplGlfw? = null
private var imGuiGl3: ImGuiImplGl3? = null
private var renderReady: Boolean = false

fun initializeImGui(fonts: List<Typeface>, window: Long) {
  imguiInitialized = true
  ImGui.createContext()
  val io = ImGui.getIO()
  io.iniFilename = null
  io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard)
  io.addConfigFlags(ImGuiConfigFlags.DockingEnable)
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

fun defineEditorGui(editor: Editor): Editor {
  val state = editor.state
  if (!imguiInitialized)
    return editor.copy(
        state.copy(
            viewportBoundsMap = mapOf()
        )
    )

  if (renderReady)
    return editor

  renderReady = true
  imGuiGlfw!!.newFrame()
  ImGui.newFrame()

  return drawEditor(editor)
}

fun prepareEditorGui(fonts: List<Typeface>, window: Long, isActive: Boolean, editor: Editor?): Editor? {
  return if (isActive && editor != null) {
    ensureImGuiIsInitialized(fonts, window)
    defineEditorGui(editor)
  } else
    editor
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
