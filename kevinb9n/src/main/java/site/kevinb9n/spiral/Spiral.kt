package site.kevinb9n.spiral

import java.awt.*
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.*

object Spiral {
  // unique real root of cos(pi/18) x^3 + sin(2pi/18) x^2 - cos(3pi/18) x - sin(4pi/18)
  // found with wolfram alpha
  private const val RATIO = 1.061118264558831
  private const val ANGLE = 4 * PI / 9 // 2 full rotations = 9 turns
  private const val POINTS = 200
  private const val SEGMENTS = POINTS - 1
  private const val TRIANGLES = SEGMENTS - 5

  private const val WINDOW_WIDTH = 1300
  private const val WINDOW_HEIGHT = 1300
  private const val INITIAL_LENGTH = 2.0

  private val COLORS = arrayOf(
    Color(255, 93, 93),
    Color(147, 201, 93),
    Color(93, 201, 147),
    Color(93, 93, 255),
    Color(201, 93, 147),
    Color(201, 147, 93),
    Color(93, 255, 93),
    Color(93, 147, 201),
    Color(147, 93, 201))

  object Canvas : JComponent() {
    override fun paintComponent(g: Graphics) {
      require(g is Graphics2D)
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g.translate(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2)
      g.scale(1.0, -1.0) // y+ goes UP, dammit

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

      // Now make the colored triangles
      for (i in 0 until TRIANGLES) {
        val triangle = polygon(points[i], points[i + 1], points[i + 5])
        g.paint = COLORS[i % 9]
        g.fillPolygon(triangle)
      }

      // Lastly draw the "spiral"
      g.paint = Color.BLACK
      var thickness = 0.3
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
}

fun main(args: Array<String>) {
  SwingUtilities.invokeLater {
    val panel = JPanel(BorderLayout())
    panel.add(Spiral.Canvas, BorderLayout.CENTER)
    val frame = JFrame()
    frame.contentPane.add(panel)
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.isVisible = true
  }
}
