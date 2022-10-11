package site.kevinb9n.javafx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import site.kevinb9n.plane.Point
import site.kevinb9n.plane.Circle
import site.kevinb9n.plane.stuff.makeCircle
import kotlin.math.roundToInt

fun enclose(points: List<Point>): Circle {
  return makeCircle(points)
}
fun main() = Application.launch(EnclosingCircleDemo::class.java)
class EnclosingCircleDemo : Application() {
  override fun start(stage: Stage) {
    val numPoints = Math.pow(2.0, 1.0 + Math.random() * 5.0).roundToInt()
    require(numPoints in 2 until 64)
    val points = generateSequence { randomPoint() }.take(numPoints).toList()
    require(points.size == numPoints)
    println(points)
    val enclosingCircle: Circle = enclose(points)
    println(enclosingCircle)
    val root = Pane()
    root.children += toJavafx(enclosingCircle).also {
      it.strokeWidth = 8.0
      it.fill = Color.TRANSPARENT
      it.stroke = Color.DARKGREEN
    }
    val edgePoints = points.filter { it.distance(enclosingCircle.center) >= enclosingCircle.radius - 1e-8 }
    root.children += edgePoints.map {
      val antipode = it + (enclosingCircle.center - it) * 2.0
      toJavafx(Circle(antipode, 8.0)).also {
        it.fill = Color.DARKGREEN
      }
    }
    root.children += points.map {
      toJavafx(Circle(it, 8.0)).also {
        it.fill = Color.DARKRED
      }
    }


    stage.title = "Enclosing circle demo"
    stage.scene = Scene(root, 1000.0, 1000.0)
    stage.show()
  }

  private fun toJavafx(circle: Circle) = javafx.scene.shape.Circle(
    circle.center.x, circle.center.y, circle.radius)

  private fun randomPoint() = Point(200.0 + 600.0 * Math.random(), 200.0 + 600.0 * Math.random())
}
