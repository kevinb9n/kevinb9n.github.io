package site.kevinb9n.javafx

import javafx.application.Application
import javafx.beans.binding.Bindings.divide
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.VPos
import javafx.scene.Scene
import javafx.scene.layout.Background
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.text.Font.font
import javafx.scene.text.FontWeight.EXTRA_BOLD
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.stage.Stage

fun main() = Application.launch(ComplexViz::class.java)

val STROKE_WIDTH = 2.0
val DOT_WIDTH = 18.0
val scaleProperty = SimpleDoubleProperty(120.0)

class ComplexViz : Application() {
  override fun start(stage: Stage) {

    val backgroundShapeList = listOf(
      // X-axis
      Line(-3.0, 0.0, 3.0, 0.0).apply {
        stroke = Color.DARKGRAY
      },
      // Y-axis
      Line(0.0, -3.0, 0.0, 3.0).apply {
        stroke = Color.DARKGRAY
      },
      // Unit circle
      Circle(0.0, 0.0, 1.0).apply {
        stroke = Color.LIGHTGRAY
        fill = Color.TRANSPARENT
      },
    )
    backgroundShapeList.forEach {
      it.strokeWidthProperty().bind(divide(STROKE_WIDTH, scaleProperty))
    }
    val coordPlane = StackPane().also {
      it.children += backgroundShapeList
      it.scaleXProperty().bind(scaleProperty)
      it.scaleYProperty().bind(scaleProperty)
    }

    val a = ScalableLabeledDot("A", Color.DARKGREEN)
    val dotScale = divide(DOT_WIDTH, scaleProperty)
    a.scaleXProperty().bind(dotScale)
    a.scaleYProperty().bind(dotScale)
//    a.translateX = sqrt(3.0)/2.0
//    a.translateY = 0.5
    coordPlane.children += a

    val root = StackPane()
    root.children += coordPlane

    with(stage) {
      scene = Scene(root, 1200.0, 900.0)
      title = "Complex visualizer"
      show()
    }
  }

  class ScalableLabeledDot(label: String, color: Color) : Pane() {
    init {
      setDimensions(1.0, 1.0)
      background = Background.fill(Color.LIGHTCORAL)
      val dot = Circle().also {
        it.centerX = 0.5
        it.centerY = 0.5
        it.radius = 0.5
        it.strokeWidth = 0.0
        it.fill = color
      }
      children += dot

      val text = Text(0.5, 0.5, label).also {
        it.textOrigin = VPos.CENTER
        it.font = font("", EXTRA_BOLD, 0.7)
        it.fill = Color.LIGHTGRAY
        it.stroke = Color.LIGHTGRAY
        it.strokeWidth = 0.01
        it.textAlignment = TextAlignment.CENTER
      }
      children += text
    }
  }
}

fun Pane.setDimensions(width: Double, height: Double) {
  minWidth = width
  prefWidth = width
  maxWidth = width
  minHeight = height
  prefHeight = height
  maxHeight = height
}
