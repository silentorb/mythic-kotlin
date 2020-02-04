package silentorb.metaview.front

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import silentorb.metaview.common.*
import silentorb.metaview.common.views.*
import silentorb.metaview.front.views.previewView
import silentorb.metaview.front.views.toolBarView
import silentorb.metaview.texturing.*
import silentorb.mythic.imaging.newTextureEngine
import org.joml.Vector2i
import java.net.URL

fun getResourceUrl(path: String): URL {
  val classloader = Thread.currentThread().contextClassLoader
  return classloader.getResource(path)
}

const val textureLength = 512

val connectableTypes = setOf(bitmapType, grayscaleType, normalsType, positionsType, depthsType)

val commonListener = wrapStateListener<AppState, CommonState>({ it.common }) { a, b -> a.copy(common = b) }

val commonSideEffectListener = wrapSideEffectListener<AppState, CommonState> { it.common }

val texturingWrapper = wrapStateListener<AppState, TexturingState>({ it.texturing }) { a, b -> a.copy(texturing = b) }

val configSaving: SideEffectStateListener<AppState> = { change ->
  val state = change.next
  val previousState = change.previous
  if (state.common.gui != previousState.common.gui || state.texturing != previousState.texturing) {
    saveConfig(state)
  }
}

fun graphSaving(): SideEffectStateListener<AppState> = { change ->
  val state = change.next
  val previousState = change.previous
  if (state.common.graph != null && ((state.common.gui.activeGraph != null && state.common.gui.activeGraph == previousState.common.gui.activeGraph) || (state.common.graph != previousState.common.graph && previousState.common.graph != null))) {
    saveJsonFile(texturePath(state.common, state.common.gui.activeGraph!!), state.common.graph!!)
  }
}

fun newValueDisplays(): ValueDisplayMap =
    textureValueDisplays(Vector2i(textureLength))

fun coreLogic(root: BorderPane, engine: Engine) {
  val rightPanel = VBox()
  rightPanel.prefWidth = 400.0
  rightPanel.children.addAll(VBox(), VBox())

  root.right = rightPanel
  val graphContainer = ScrollPane()
  root.center = graphContainer

  val transformListeners: List<StateTransformListener<AppState>> = listOf<StateTransformListener<AppState>>(
      domainListener(engine, nodeDefinitions),
      texturingWrapper(texturingListener)
  )
      .plus(listOf<StateTransformListener<CommonState>>(
          commonStateListener(engine, nodeDefinitions) { getFocus(root) },
          stateTransformListener(graphTransform(onNewGraph)),
          onGraphChanged(nodeDefinitions, fillerTypeValues(textureLength), engine),
          historyStateListener(10)
      )
          .map(commonListener))

  val sideEffectListeners: MutableList<SideEffectStateListener<AppState>> = mutableListOf()
  val valueDisplays = newValueDisplays()
  val initialState = newState()

  val (emit, getState) = appLogic(transformListeners, sideEffectListeners, initialState)

  root.left = textureList(emit, initialState.common)
  rightPanel.children.set(1, propertiesView(nodeDefinitions, engine, emit)(initialState.common))
  graphContainer.content = graphView(engine, nodeDefinitions, connectableTypes, valueDisplays, emit, initialState.common)

  val updateTextureListView: SideEffectStateListener<AppState> = { change ->
    val previousState = change.previous
    val newState = change.next
    if (!change.event.preview && newState.common.graphNames.size != previousState.common.graphNames.size || newState.domain != previousState.domain) {
      root.left = textureList(emit, change.next.common)
    }
  }

  val updateGraphView = commonSideEffectListener(
      graphViewListener(engine, nodeDefinitions, connectableTypes, valueDisplays, emit) {
        graphContainer.content = null // JavaFX has some weird caching/race condition that this prevents
        graphContainer.content = it
      })

  val updatePreviewView: SideEffectStateListener<AppState> = { change ->
    rightPanel.children.set(0, previewView(engine, nodeDefinitions, valueDisplays, emit)(change.next.common))
  }

  val updatePropertiesView: SideEffectStateListener<AppState> = { change ->
    val state = change.next.common
    val previousState = change.previous.common
    if (!change.event.preview && state.gui.activeGraph != previousState.gui.activeGraph || state.gui.graphInteraction.nodeSelection != previousState.gui.graphInteraction.nodeSelection) {
      rightPanel.children.set(1, propertiesView(nodeDefinitions, engine, emit)(state))
    }
  }

  sideEffectListeners.addAll(listOf(
      updateTextureListView,
      updateGraphView,
      updatePreviewView,
      updatePropertiesView,
      configSaving,
      graphSaving()
  ))

  root.top = VBox(5.0, menuBarView(emit), toolBarView(initialState, emit))

  listenForKeypresses(nodeDefinitions, root, emit, { getState().common })

  emit(Event(CommonEvent.refresh))
}

class LabGui : Application() {

  override fun start(primaryStage: Stage) {
    try {
      primaryStage.title = "Texture Generation"

      val engine = newTextureEngine(Vector2i(textureLength))

      val root = BorderPane()
      val scene = Scene(root, 1600.0, 800.0)
      val s = getResourceUrl("style.css")
      scene.getStylesheets().add(s.toString())
      primaryStage.scene = scene
      _globalWindow = scene.window

      coreLogic(root, engine)
      primaryStage.show()
//      primaryStage.isMaximized = true
    } catch (exception: Exception) {
      println(exception.stackTrace)
    }
  }

  companion object {
    @JvmStatic
    fun mainMenu(args: List<String>) {
      Application.launch(LabGui::class.java)
    }
  }
}

object MetaView {
  @JvmStatic
  fun main(args: Array<String>) {
    LabGui.mainMenu(listOf())
  }
}
