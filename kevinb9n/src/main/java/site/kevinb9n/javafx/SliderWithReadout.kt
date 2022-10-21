package site.kevinb9n.javafx

import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.format
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.layout.VBox

class SliderWithReadout(
        val slider: Slider,
        name: String,
        fineTune: Double,
        format: String,
        tooltip: String = "") : VBox(6.0) {
  val valueProperty = SimpleDoubleProperty()
  val label = Label().also {
    it.textProperty().bind(format("$name = $format", valueProperty))
    it.tooltip = Tooltip(tooltip)
  }
  init {
    style = """
        -fx-background-color: white;
        -fx-border-color: black;
        -fx-border-width: 1;
        -fx-border-radius: 6;
        -fx-padding: 8;
    """
    children += label
    children += slider

    slider.tooltip = Tooltip(tooltip)
    slider.blockIncrement = fineTune
    valueProperty.bind(slider.valueProperty())
  }
}
