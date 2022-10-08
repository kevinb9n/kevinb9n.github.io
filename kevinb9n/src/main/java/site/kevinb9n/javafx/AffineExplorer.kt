package site.kevinb9n.javafx

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import site.kevinb9n.plane.Point

fun main() = Application.launch(AffineExplorer::class.java)

class AffineExplorer : Application() {
  override fun start(stage: Stage) {
    val triangle = pointsToPolygon(Point(0, 0), Point(50, 50), Point(100, 50), Point(100, 100), Point(0, 100)).apply {
      strokeWidth = 5.0
      stroke = Color.BLACK
    }

    val group = Group(triangle)
    val outerGroup = Pane(group)
    val sceneRoot = BorderPane().apply {
      right = VBox(4.0).apply {
        children += VBox(4.0).apply {
          children += sliderPanel("TranslateX", -100..1600, 750, group.translateXProperty())
          children += sliderPanel("TranslateY", -100..1200, 550, group.translateYProperty())
          children += sliderPanel("Scale", -20..20, 1, group.scaleXProperty(), group.scaleYProperty())
          children += sliderPanel("Rotation", -180..180, 0, group.rotateProperty())

          children += sliderPanel("Outer TranslateX", -800..800, 0, outerGroup.translateXProperty())
          children += sliderPanel("Outer TranslateY", -800..800, 0, outerGroup.translateYProperty())
          children += sliderPanel("Outer Scale", -20..20, 1, outerGroup.scaleXProperty(), outerGroup.scaleYProperty())
          children += sliderPanel("Outer Rotation", -180..180, 0, outerGroup.rotateProperty())

          val colorPicker = ColorPicker(Color.MEDIUMPURPLE)
          children += colorPicker
          triangle.fillProperty().bind(colorPicker.valueProperty())

          alignment = Pos.BOTTOM_LEFT
        }
        style = STYLE
        BorderPane.setAlignment(this, Pos.TOP_RIGHT)
      }
      center = Pane().apply {
        children += outerGroup
      }
    }
    with(stage) {
      title = "Affine example if I do say so myself"
      scene = Scene(sceneRoot, 1800.0, 1200.0)
      show()
    }
  }

  fun sliderPanel(label: String, range: IntRange, initial: Int, vararg props: DoubleProperty):
    VBox {
    val slider = Slider(range.start.toDouble(), range.endInclusive.toDouble(), initial.toDouble())
    slider.prefWidth = 400.0
    props.forEach { it.bind(slider.valueProperty()) }
    return VBox(4.0).apply {
      children += Label(label)
      children += slider
      children += Label().apply {
        textProperty().bind(Bindings.format("%.2f", slider.valueProperty()))
      }
      style = STYLE
    }
  }

  val STYLE = ("-fx-background-color: white;"
    + "-fx-border-color: black;"
    + "-fx-border-width: 1;"
    + "-fx-border-radius: 6;"
    + "-fx-padding: 8;")

}
