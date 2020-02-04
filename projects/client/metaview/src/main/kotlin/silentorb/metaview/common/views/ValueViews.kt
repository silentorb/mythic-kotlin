package silentorb.metaview.common.views

import com.sun.javafx.scene.control.CustomColorDialog
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.util.Duration
import silentorb.metaview.common.*

import silentorb.mythic.spatial.Vector3

typealias OnChange = (Any, Boolean) -> Unit
typealias ValueView = (value: Any, OnChange) -> Node

typealias ValueViewSource = (InputDefinition) -> ValueView

fun convertColor(color: Color) =
    Vector3(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat())

val colorView: ValueView = { value, changed ->
  val color = value as Vector3
  val image = Pane()
  var fxColor = Color(color.x.toDouble(), color.y.toDouble(), color.z.toDouble(), 1.0)
  fun updateColorSample() {
    image.background = Background(BackgroundFill(fxColor, CornerRadii.EMPTY, Insets.EMPTY))
  }
  updateColorSample()
  image.prefWidth = nodeLength.toDouble()
  image.prefHeight = nodeLength.toDouble()
  image.setOnMouseClicked {
    val dialog = CustomColorDialog(globalWindow())
    dialog.currentColor = fxColor
    dialog.show()
    var lastColor = dialog.customColor
    val updater = Timeline(KeyFrame(Duration.seconds(0.5), EventHandler {
      if (dialog.customColor != lastColor) {
        lastColor = dialog.customColor
        fxColor = dialog.customColor
        updateColorSample()
        val newColor = convertColor(dialog.customColor)
        changed(newColor, true)
      }
    }))
    updater.cycleCount = Timeline.INDEFINITE
    updater.play()

    val save = {
      updater.stop()
      fxColor = dialog.customColor
      updateColorSample()
      val newColor = convertColor(dialog.customColor)
      changed(newColor, false)
    }

    dialog.setOnCancel {
      updater.stop()
      fxColor = Color(color.x.toDouble(), color.y.toDouble(), color.z.toDouble(), 1.0)
      updateColorSample()
      changed(color, true)
    }
    dialog.setOnSave(save)
    dialog.setOnUse(save)
  }
  image
}

val numericFloatView: ValueViewSource = { definition ->
  { value, changed ->
    val field = TextField()
    field.text = (value as Float).toString()
    field.textProperty().addListener { event ->
      val newValue = field.text.toFloatOrNull()
      if (newValue != null)
        changed(newValue, false)
    }
    field.setOnInputMethodTextChanged { event ->
    }
    field
  }
}

val numericIntView: ValueViewSource = { definition ->
  { value, changed ->
    val field = TextField()
    field.text = (value as Int).toString()
    field.textProperty().addListener { event ->
      val newValue = field.text.toIntOrNull()
      if (newValue != null)
        changed(newValue, false)
    }
    field.setOnInputMethodTextChanged { event ->
    }
    field
  }
}

val weightsView: ValueViewSource = { definition ->
  { value, changed ->
    val sliderPanel = VBox(5.0)
    var weights = value as List<Float>
    var listen = true
    var sliders: List<Slider> = listOf()

    val lockBoxes = weights.mapIndexed { i, weight ->
      CheckBox()
    }

    val getLocks = {
      lockBoxes.map { it.isSelected }
    }

    val updateSliders = { index: Int ->
      listen = false
      sliders.forEachIndexed { ri, s ->
        if (ri != index) {
          s.value = weights[ri].toDouble()
        }
      }
      listen = true
    }

    sliders = weights.mapIndexed { i, weight ->
      val slider = Slider(0.0, 1.0, weight.toDouble())
      slider.valueProperty().addListener { _ ->
        if (listen) {
          println("a $i")
          weights = balanceWeights(i, slider.value.toFloat(), getLocks())(weights)
          updateSliders(i)
          listen = true
          changed(weights, true)
        }
      }

      slider.setOnMouseReleased {
        if (listen) {
          println("b $i")
          weights = balanceWeights(i, slider.value.toFloat(), getLocks())(weights)
          changed(weights, false)
        }
      }
      slider
    }

    val rows = lockBoxes.zip(sliders) { lock, slider ->
      HBox(5.0, lock, slider)
    }

    sliderPanel.children.addAll(rows)
    val alignWaits = Button("Align")
    alignWaits.setOnMouseClicked {
      val locks = getLocks()
      val lockedAmount = weights.filterIndexed { i, _ -> locks[i] }.sum()
      val newValue = (1f - lockedAmount) / weights.size.toFloat()

      weights = weights.mapIndexed { i, currentValue ->
        if (locks[i])
          currentValue
        else
          newValue
      }

      updateSliders(-1)
      changed(weights, false)
    }
    HBox(5.0, sliderPanel, alignWaits)
  }
}

val valueViews: Map<String, ValueViewSource> = mapOf(
    colorType to { _ -> colorView },
    floatType to numericFloatView,
    intType to numericIntView,
    weightsType to weightsView
)
