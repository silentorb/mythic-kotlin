package silentorb.mythic.editing

import imgui.ImFontConfig
import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.GLFW.glfwGetCurrentContext
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent

private var imguiInitialized: Boolean = false
private var imGuiGlfw: ImGuiImplGlfw? = null
private var imGuiGl3: ImGuiImplGl3? = null

fun initializeImGui(fonts: List<Typeface>, window: Long) {
  imguiInitialized = true
  ImGui.createContext()
  val io = ImGui.getIO()
  io.iniFilename = null
  io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard)
  io.addConfigFlags(ImGuiConfigFlags.DockingEnable)
//  io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable)
  io.configViewportsNoTaskBarIcon = true
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

fun updateEditorGui() {
  if (!imguiInitialized)
    return

  imGuiGlfw!!.newFrame()
  ImGui.newFrame()

  drawEditor()

  ImGui.render()
  imGuiGl3!!.renderDrawData(ImGui.getDrawData())

//  val window = glfwGetCurrentContext()
//  ImGui.updatePlatformWindows()
//  ImGui.renderPlatformWindowsDefault()
//  glfwMakeContextCurrent(window)
}
