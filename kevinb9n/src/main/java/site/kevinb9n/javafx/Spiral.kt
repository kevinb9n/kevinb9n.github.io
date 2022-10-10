package site.kevinb9n.javafx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import javafx.stage.Stage
import site.kevinb9n.plane.Angle
import site.kevinb9n.plane.Point
import site.kevinb9n.plane.CartesianVector
import site.kevinb9n.plane.PolarVector

const val WIDTH = 2400.0
const val HEIGHT = 1350.0
const val POINTS = 150
const val INITIAL_LENGTH = 2.0
const val INITIAL_THICKNESS = 0.2
val THICKNESS_RATIO = 1.033

fun main() = Application.launch(Spiral::class.java)

const val SEGMENTS = POINTS - 1
const val TRIANGLES = SEGMENTS - 5

// unique real root of cos(pi/18) x^3 + sin(2pi/18) x^2 - cos(3pi/18) x - sin(4pi/18)
// found with wolfram alpha
const val RATIO = 1.061118264558831

const val ANGLE = 80 // takes 9 segments to make 2 full rotations
private val TRIANGLE_COLORS = arrayOf(
  Color.rgb(227, 52, 47), // 1
  Color.rgb(255, 237, 74), // 3
  Color.rgb(77, 192, 181), // 5
  Color.rgb(101, 116, 205), // 7
  Color.rgb(246, 109, 155), // 9
  Color.rgb(246, 153, 63), // 2
  Color.rgb(56, 193, 114), // 4
  Color.rgb(52, 144, 220), // 6
  Color.rgb(149, 97, 226), // 8
).map { c -> c.desaturate() }

private val SPIRAL_COLOR = Color.BLACK

class Spiral : Application() {
  override fun start(stage: Stage) {
    // First calculate the points
    var heading = 0.0
    var length = INITIAL_LENGTH
    val points = mutableListOf<Point>()
    var next = Point(WIDTH / 2, HEIGHT / 2)
    for (i in 0 until SEGMENTS) {
      points += next
      next = next + PolarVector(length, Angle.fromDegrees(heading))
      heading = (heading + ANGLE).mod(360.0)
      length *= RATIO
    }

    val canvas = Canvas(WIDTH, HEIGHT)
    val g = canvas.graphicsContext2D

    g.fill = SPIRAL_COLOR
    g.drawPolygon(points.subList(0, 5))

    // Now make the colored triangles
    for (i in 0 until TRIANGLES) {
      g.fill = TRIANGLE_COLORS[i % 9]
      g.drawPolygon(listOf(points[i], points[i + 1], points[i + 5]))
    }

    // Lastly draw the "spiral"
    g.lineWidth = INITIAL_THICKNESS
    g.lineCap = StrokeLineCap.ROUND
    g.stroke = SPIRAL_COLOR

    for (i in 10 until SEGMENTS) { // skip the first 2 times around
      g.strokeLine(points[i-1].x, points[i-1].y, points[i].x, points[i].y)
      g.lineWidth *= THICKNESS_RATIO
    }

    val pane = Pane(canvas)
    stage.scene = Scene(pane, WIDTH, HEIGHT, false, SceneAntialiasing.BALANCED)
    stage.title = "Fall in love with a spiral, where it leads only heaven knows"
    stage.show()

    // renderToPngFile(pane, "/Users/kevinb9n/spiral.png")
  }
}
