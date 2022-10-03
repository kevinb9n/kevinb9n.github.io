package site.kevinb9n.javafx

import javafx.application.Application
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.ColorPicker
import javafx.scene.layout.Background
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Polygon
import javafx.scene.shape.Shape
import javafx.scene.shape.StrokeLineJoin
import javafx.stage.Stage
import java.lang.Math.toRadians
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

fun main() = Application.launch(Triangles::class.java)

class Triangles : Application() {
  val WIN_WIDTH = 2000.0
  val WIN_HEIGHT = 1200.0
  val MARGIN = 50.0
  val SHAPE_COUNT = 42
  val REAL_STROKE = 0.9

  val BACKGROUND = "#d8cab2"
  val COLORS = listOf(
    "0f191912", // gray
    "d4001c0b", // crimson
    "692d000b", // brown!
    "ee84000b", // orange
    "ecc5000b", // yellow
    "1399070b", // green
    "0cc9050b", // soothing green
    "0cbfd90b", // aqua
    "0089de0b", // other aqua
    "0ab7ad0b", // vivid aqua
    "00b1790b", // turquoise?
    "0e5faf0b", // vivid blue
    "1185ca0b", // soft blue
    "0869a30b", // another lovely blue!
    "0089de0b", // another lovely blue!
    "1555bb0b", // perfect blue
    "2234970b", // indigo
    "4d0e930b", // purple
    "9e14b50b", // soothing purple
  )

  val USABLE = box(Point(MARGIN, MARGIN), Point(WIN_WIDTH - MARGIN, WIN_HEIGHT - MARGIN * 2))

  override fun start(stage: Stage) {
    val offsetDistance = snapRandom(2.0)
    val offsetAngle = snapRandom(90.0)
    val offsetX = round(offsetDistance * cos(toRadians(offsetAngle)))
    val offsetY = round(offsetDistance * sin(toRadians(offsetAngle)))

    val rotation = round(snapRandom(90.0 / (SHAPE_COUNT - 1)))

    val color = average(Color.web(COLORS.random()), Color.web(COLORS.random()))
    val shapeType = ShapeType.values().random()

    val path = Path.of("/Users/kevinb9n/triangles.txt")
    val desc = "$shapeType, offset ($offsetX, $offsetY), rotation $rotation\n"
    println(desc)
    Files.writeString(path, desc)

    val colorPicker = ColorPicker(color)

    val triangles = (0 until SHAPE_COUNT).map { param ->
      val height = param.toDouble()
      val base = SHAPE_COUNT - 1 - height
      shapeType.centeredPolygon(base, height).apply {
        // max sat and opaq, dim to 30%
        this.stroke = color.deriveColor(0.0, 10.0, 0.4, 15.0)
        fillProperty().bind(colorPicker.valueProperty())
        strokeLineJoin = StrokeLineJoin.ROUND
        strokeWidth = .05 // temporary

        translateX = param * offsetX
        translateY = param * offsetY
        rotate = param * rotation
      }
    }
    val stack = Group()
    stack.children += triangles

    // Maximize width and minimize height by rotating the whole image
    // Brute force, why not?
    val goodAngle = (0..170 step 10).minByOrNull {
      stack.rotate = it.toDouble()
      stack.boundsInParent.height
    }!!
    val bestAngle = ((goodAngle - 9)..(goodAngle + 9)).minByOrNull {
      stack.rotate = it.toDouble()
      stack.boundsInParent.height
    }!!

    stack.rotate = bestAngle.toDouble()
    val outer = Group()
    outer.children += stack

    var scale = scaleToFit(outer.boundsInLocal, USABLE)
    stack.children.filterIsInstance(Shape::class.java).forEach {
      it.strokeWidth = REAL_STROKE / scale
    }
    scale = scaleToFit(outer.boundsInLocal, USABLE) // ok it could maybe have changed a TINY bit
    outer.scaleX = scale
    outer.scaleY = scale

    outer.translateX = USABLE.centerX - outer.boundsInParent.centerX
    outer.translateY = USABLE.centerY - outer.boundsInParent.centerY

    val sceneRoot = BorderPane()
    val pretty = Pane(outer).apply { background = Background.fill(Paint.valueOf(BACKGROUND)) }
    sceneRoot.center = pretty
    sceneRoot.bottom = colorPicker

    val scene = Scene(sceneRoot, WIN_WIDTH, WIN_HEIGHT)

    stage.scene = scene
    stage.show()
    renderToPngFile(pretty, "/Users/kevinb9n/triangles.png")
  }

  private fun average(c1: Color, c2: Color): Color {
    return Color.hsb(
      averageHues(c1.hue, c2.hue),
      (c1.saturation + c2.saturation) / 2.0,
      (c1.brightness + c2.brightness) / 2.0,
      (c1.opacity + c2.opacity) / 2.0)
  }

  private fun averageHues(hue1: Double, hue2: Double): Double {
    val avg = (hue1 + hue2) / 2.0
    return if (abs(hue1 - hue2) > 180.0) avg + 180.0 else avg
  }
}
data class ParametricShapeSet(
  val shapeType: ShapeType,
  val count: Int,
  val axis: Point2D, // point to rotate around
)

enum class ShapeType {
  ISOSCELES {
    override fun centeredPolygon(base: Double, height: Double) =
      polygon(
        Point(-base / 2, -height / 2),
        Point(base / 2, -height / 2),
        Point(0, height / 2))
  },
  RIGHT {
    override fun centeredPolygon(base: Double, height: Double) =
      polygon(
        Point(-base / 2, -height / 2),
        Point(base / 2, -height / 2),
        Point(base / 2, height / 2))
  },
  SLANT {
    override fun centeredPolygon(base: Double, height: Double) =
      polygon(
        Point(0, -height / 2),
        Point(base, -height / 2),
        Point(base*height/(base+height), height / 2))
  },
  RECTANGLE {
    override fun centeredPolygon(base: Double, height: Double) =
      polygon(
        Point(-base / 2, -height / 2),
        Point(base / 2, -height / 2),
        Point(base / 2, height / 2),
        Point(-base / 2, height / 2))
  },
  ISOS_TRAP {
    override fun centeredPolygon(base: Double, height: Double) =
      polygon(
        Point(-base / 2, -height / 2),
        Point(base / 2, -height / 2),
        Point(base / 4, height / 2),
        Point(-base / 4, height / 2))
  },
  DIAMOND {
    override fun centeredPolygon(base: Double, height: Double) =
      polygon(
        Point(-base / 2, 0),
        Point(0, -height / 2),
        Point(base / 2, 0),
        Point(0, height / 2))
  },
  PGRAM {
    override fun centeredPolygon(base: Double, height: Double) =
      polygon(
        Point(0, -height / 2),
        Point(base / 2, -height / 2),
        Point(0, height / 2),
        Point(-base / 2, height / 2))
  },
  NOT_SURE {
    override fun centeredPolygon(base: Double, height: Double) : Polygon {
      val fraction = base / (base + height)
      val whaction = fraction * fraction
      val newbase = whaction * (base + height)
      val newheight = height
      return polygon(
        Point(-newbase / 2, -newheight / 2),
        Point(newbase / 2, -newheight / 2),
        Point(0, newheight / 2))
    }
  },
  ;

  abstract fun centeredPolygon(base: Double, height: Double): Polygon
}
