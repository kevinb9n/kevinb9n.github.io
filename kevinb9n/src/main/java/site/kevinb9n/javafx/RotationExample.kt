package site.kevinb9n.javafx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.stage.Stage

fun main() = Application.launch(RotationExample::class.java)

class RotationExample : Application() {
  override fun start(stage: Stage) {
    val rectangle1 = Rectangle(150.0, 75.0, 200.0, 150.0)
    rectangle1.fill = Color.BLUE
    rectangle1.stroke = Color.BLACK
    rectangle1.transforms += Scale(2.0, 2.0, 175.0, 112.5)

    val rectangle2 = Rectangle(150.0, 75.0, 200.0, 150.0)
    rectangle2.fill = Color.BURLYWOOD
    rectangle2.stroke = Color.BLACK

    val root = StackPane(rectangle1, rectangle2)
    stage.title = "Rotation transformation example"
    stage.scene = Scene(root, 600.0, 300.0)
    stage.show()
  }
}
