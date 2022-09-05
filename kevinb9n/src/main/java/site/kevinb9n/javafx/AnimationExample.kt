package site.kevinb9n.javafx

import javafx.animation.RotateTransition
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.stage.Stage
import javafx.util.Duration

fun main() = Application.launch(AnimationExample::class.java)

class AnimationExample : Application() {
  override fun start(stage: Stage) {
    val hexagon = Polygon()
    hexagon.getPoints().addAll(arrayOf(
      200.0, 50.0,
      400.0, 50.0,
      450.0, 150.0,
      400.0, 250.0,
      200.0, 250.0,
      150.0, 150.0))
    hexagon.setFill(Color.BLUE)
    val rotateTransition = RotateTransition()
    rotateTransition.duration = Duration.millis(1000.0)
    rotateTransition.node = hexagon
    rotateTransition.byAngle = 360.0
    rotateTransition.cycleCount = 50
    rotateTransition.isAutoReverse = false
    rotateTransition.play()
    val root = Group(hexagon)
    val scene = Scene(root, 600.0, 300.0)
    stage.title = "Rotate transition example"
    stage.scene = scene
    stage.show()
  }
}
