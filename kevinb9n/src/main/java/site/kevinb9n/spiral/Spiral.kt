package site.kevinb9n.spiral

import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Polygon
import java.awt.RenderingHints
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

const val WINDOW_WIDTH = 2400
const val WINDOW_HEIGHT = 1350
const val POINTS = 150
const val INITIAL_LENGTH = 3.0
const val INITIAL_THICKNESS = 0.25

fun main() {
  SwingUtilities.invokeLater {
    val panel = JPanel(BorderLayout())
    panel.add(Canvas, BorderLayout.CENTER)
    val frame = JFrame()
    frame.contentPane.add(panel)
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
  }
}

const val SEGMENTS = POINTS - 1
const val TRIANGLES = SEGMENTS - 5

// unique real root of cos(pi/18) x^3 + sin(2pi/18) x^2 - cos(3pi/18) x - sin(4pi/18)
// found with wolfram alpha
const val RATIO = 1.061118264558831
const val TAU = 2 * PI // (since it'll eventually be added to the libraries :-))

const val ANGLE = TAU * 2 / 9 // takes 9 segments to make 2 full rotations
private val TRIANGLE_COLORS = arrayOf(
  Color(227, 52, 47), // 1
  Color(255, 237, 74), // 3
  Color(77, 192, 181), // 5
  Color(101, 116, 205), // 7
  Color(246, 109, 155), // 9
  Color(246, 153, 63), // 2
  Color(56, 193, 114), // 4
  Color(52, 144, 220), // 6
  Color(149, 97, 226), // 8
)

private val SPIRAL_COLOR = Color.BLACK

object Canvas : JComponent() {
  override fun paintComponent(g: Graphics) {
    require(g is Graphics2D)
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.translate(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2)
//    g.scale(1.0, -1.0) // usually want +y to go *up* but this doesn't matter

    // First calculate the points
    var heading = 0.0
    var length = INITIAL_LENGTH
    val pts = arrayOfNulls<Point2D.Double>(POINTS)
    pts[0] = Point2D.Double()
    for (i in 1 until SEGMENTS) {
      pts[i] = translateByDistanceAtAngle(pts[i - 1]!!, length, heading)
      heading += ANGLE
      length *= RATIO
    }
    @Suppress("UNCHECKED_CAST")
    val points = pts as Array<Point2D.Double>

    // Fill the center
    g.paint = SPIRAL_COLOR
    g.fillPolygon(polygon(points[0], points[1], points[2], points[3], points[4]))

    // Now make the colored triangles
    for (i in 0 until TRIANGLES) {
      val triangle = polygon(points[i], points[i + 1], points[i + 5])
      g.paint = TRIANGLE_COLORS[i % 9]
      g.fillPolygon(triangle)
    }

    // Lastly draw the "spiral"
    g.paint = SPIRAL_COLOR
    var thickness = INITIAL_THICKNESS
    for (i in 1 until SEGMENTS) {
      g.stroke = BasicStroke(thickness.toFloat(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
      g.draw(Line2D.Double(points[i - 1], points[i]))
      thickness *= sqrt(RATIO)
    }
  }

  override fun getPreferredSize(): Dimension {
    return Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)
  }
}

fun polygon(vararg points: Point2D) = Polygon().apply {
  points.forEach { addPoint(it.x.roundToInt(), it.y.roundToInt()) }
}

fun translateByDistanceAtAngle(
  point: Point2D.Double, distance: Double, angle: Double) = Point2D.Double(
  point.x + distance * cos(angle),
  point.y + distance * sin(angle))
