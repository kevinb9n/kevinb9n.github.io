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
    hexagon.points += arrayOf(
      203.0, 150.0,
      251.5, 66.0,
      348.5, 66.0,
      397.0, 150.0,
      348.5, 234.0,
      251.5, 234.0)
    hexagon.setFill(Color.BLUE)

    RotateTransition().apply {
      duration = Duration.millis(10000.0)
      node = hexagon
      byAngle = 3600.0
      cycleCount = 100
      isAutoReverse = false
      play()
    }
    val root = Group(hexagon)
    stage.title = "Rotate transition example"
    stage.scene = Scene(root, 600.0, 300.0)
    stage.show()
  }
}
