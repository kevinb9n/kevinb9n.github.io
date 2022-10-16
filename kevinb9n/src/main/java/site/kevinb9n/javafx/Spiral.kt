package site.kevinb9n.javafx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import javafx.stage.Stage
import site.kevinb9n.plane.Angle
import site.kevinb9n.plane.Point
import site.kevinb9n.plane.Vector
import site.kevinb9n.plane.Vector.Companion.vector

fun main() = Application.launch(Spiral::class.java)

const val WIDTH = 2400.0
const val HEIGHT = 1350.0
const val INITIAL_LENGTH = 3.0
const val INITIAL_THICKNESS = 0.25
const val THICKNESS_RATIO = 1.035
const val POINTS = 150

private val FILL_COLORS = arrayOf(
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

private val EDGE_COLOR = Color.BLACK
private const val NUM_EDGES = POINTS - 1
private const val NUM_TRIANGLES = NUM_EDGES - 5

class Spiral : Application() {
  private val ZERO_EDGE = Edge(
    Point(WIDTH / 2, HEIGHT / 2),
    vector(direction = Angle.ZERO, magnitude = INITIAL_LENGTH))

  override fun start(stage: Stage) {
    val points = generateSequence(ZERO_EDGE) { it.next() }
      .take(POINTS)
      .map { it.p }
      .toList()

    val root = Pane()
    root.children += Canvas(WIDTH, HEIGHT).apply {
      with(graphicsContext2D) {
        drawCenter(points)
        drawTriangles(points)
        drawEdges(points)
      }
    }

    stage.scene = Scene(root, WIDTH, HEIGHT, false, SceneAntialiasing.BALANCED)
    stage.title = "Fall in love with a spiral, where it leads only heaven knows"
    stage.show()
    // renderToPngFile(root, "/Users/kevinb9n/spiral.png")
  }

  private fun GraphicsContext.drawCenter(points: List<Point>) {
    fill = averageColors(FILL_COLORS).darker().desaturate()
    drawPolygon(points.subList(0, 5))
  }

  private fun GraphicsContext.drawTriangles(points: List<Point>) {
    for (i in 0 until NUM_TRIANGLES) {
      fill = FILL_COLORS[i % 9]
      drawPolygon(listOf(points[i], points[i + 1], points[i + 5]))
    }
  }

  private fun GraphicsContext.drawEdges(points: List<Point>) {
    lineWidth = INITIAL_THICKNESS
    lineCap = StrokeLineCap.ROUND
    stroke = EDGE_COLOR
    points.drop(10).windowed(2).forEach {
      drawEdge(it[0], it[1])
      lineWidth *= THICKNESS_RATIO
    }
  }

  private fun GraphicsContext.drawEdge(from: Point, to: Point) = strokeLine(from.x, from.y, to.x, to.y)

  // This is just stupidness :-)
  private fun averageColors(colors: List<Color>): Color {
    // dumm hakk
    val hue = colors.map { (it.hue + 270) % 360 }.average() + 90
    val sat = colors.map { it.saturation }.average()
    val brt = colors.map { it.brightness }.average()
    return Color.hsb(hue, sat, brt)
  }
}

private data class Edge(val p: Point, val v: Vector) {
  fun next() = Edge(p + v, v * RATIO + ANGLE)

  companion object {
    // root of cos(pi/18) x^3 + sin(2pi/18) x^2 - cos(3pi/18) x - sin(4pi/18)
    private val RATIO = 1.061118264558831
    private val ANGLE = Angle.turns(2.0 / 9.0)
  }
}
