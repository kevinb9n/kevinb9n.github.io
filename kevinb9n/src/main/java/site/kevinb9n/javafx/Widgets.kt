package site.kevinb9n.javafx

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.RadioButton
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

fun main() = Application.launch(Widgets::class.java)

class Widgets : Application() {
  override fun start(stage: Stage) {
    val sceneRoot = BorderPane().apply {
      right = VBox(4.0).apply {
        children += listOf(loginPanel(), confirmationPanel(), progressPanel())
        style = STYLE
        BorderPane.setAlignment(this, Pos.TOP_RIGHT)
      }
    }
    with(stage) {
      title = "Widgets n Shit"
      scene = Scene(sceneRoot, 1200.0, 900.0)
      show()
    }
  }

  fun toggleGroup(selected: Int? = null, vararg radios: RadioButton):
    Array<out RadioButton> {
    val toggle = ToggleGroup()
    for (i in radios.indices) {
      radios[i].toggleGroup = toggle
      if (i == selected) radios[i].isSelected = true
    }
    return radios
  }

  fun loginPanel(): Node {
    return HBox(6.0).apply {
      children += VBox(4.0).apply {
        children += toggleGroup(0,
          RadioButton("High"),
          RadioButton("Medium"),
          RadioButton("Low"))
      }
      children += VBox(4.0).apply {
        children += TextField().apply {
          prefColumnCount = 10
          promptText = "Your name"
        }
        children += PasswordField().apply {
          prefColumnCount = 10
          promptText = "Your password"
        }
        children += ChoiceBox<String>().apply {
          items += listOf("English", "Русский", "Français")
          tooltip = Tooltip("Your language")
          selectionModel.select(0)
        }
      }
      alignment = Pos.BOTTOM_LEFT
      style = STYLE
    }
  }

  private fun confirmationPanel(): Node {
    val label = Label("Not Available")
    return HBox(6.0).apply {
      children += Button("Accept").apply {
        onAction = EventHandler { label.text = "Accepted" }
      }
      children += Button("Decline").apply {
        onAction = EventHandler { label.text = "Declined" }
      }
      children += label
      style = STYLE
      alignment = Pos.CENTER_LEFT
    }
  }

  private fun progressPanel(): Node {
    val slider = Slider()
    val progressIndicator = ProgressIndicator(0.0)
    progressIndicator.progressProperty().bind(
      Bindings.divide(slider.valueProperty(), slider.maxProperty()))
    return HBox(6.0).apply {
      children += listOf(Label("Progress:"), slider, progressIndicator)
      style = STYLE
    }
  }

  val STYLE = ("-fx-background-color: white;"
    + "-fx-border-color: black;"
    + "-fx-border-width: 1;"
    + "-fx-border-radius: 6;"
    + "-fx-padding: 8;")

}
