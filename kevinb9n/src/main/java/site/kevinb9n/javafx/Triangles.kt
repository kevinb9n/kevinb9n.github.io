package site.kevinb9n.javafx

import com.google.common.math.IntMath
import javafx.application.Application
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Paint
import javafx.scene.shape.Polygon
import javafx.scene.shape.Shape
import javafx.scene.shape.StrokeLineJoin
import javafx.stage.Stage
import java.lang.Math.random
import java.lang.Math.toRadians
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun main() = Application.launch(Triangles::class.java)

class Triangles : Application() {
  val WIN_WIDTH = 2000.0
  val WIN_HEIGHT = 1200.0
  val MARGIN = 40.0
  val SHAPE_COUNT = 57 // okay so there's really one more shape than this
  val REAL_STROKE = 1.0

  val BACKGROUND = "#d8cab2"
  val COLORSES = listOf(
    Colors(stroke = "#550011", fill = "#aa002207"),
    Colors(stroke = "#274e13", fill = "#2dc35407"),
    Colors(stroke = "#001155", fill = "#0033aa07"),
    Colors(stroke = "#29133f", fill = "#53277e07"))

  val USABLE = box(Point(MARGIN, MARGIN), Point(WIN_WIDTH - MARGIN, WIN_HEIGHT - MARGIN))

  override fun start(stage: Stage) {
    val offsetDistance = snapRandom(1.0)
    val offsetAngle = snapRandom(90.0)
    val offsetX = round(offsetDistance * cos(toRadians(offsetAngle)))
    val offsetY = round(offsetDistance * sin(toRadians(offsetAngle)))

    // at most, the two zero-width shapes can end up parallel
    val rotation = round(snapRandom(90.0 / (SHAPE_COUNT - 1)))

    val altXOffset = random() < 0.0
    val altYOffset = random() < 0.0
    val altRotate = random() < 0.0

    val colors = COLORSES.random()
    val shapeType = ShapeType.values().random()

    val xdesc = describe(offsetX, altXOffset)
    val ydesc = describe(offsetY, altYOffset)
    val rotdesc = describe(rotation, altRotate)

    val path = Path.of("/Users/kevinb9n/triangles.txt")
    val desc = "$shapeType, offset ($xdesc, $ydesc), rotation $rotdesc\n"
    println(desc)
    Files.writeString(path, desc)

    val triangles = (0 until SHAPE_COUNT).map { param ->
      val height = param.toDouble()
      val base = SHAPE_COUNT - 1 - height
      shapeType.centeredPolygon(base, height).apply {
        colors.applyTo(this)
        strokeLineJoin = StrokeLineJoin.ROUND
        strokeWidth = .05 // temporary

        translateX = param * offsetX * sign(altXOffset, param)
        translateY = param * offsetY * sign(altYOffset, param)
        rotate = param * rotation * sign(altRotate, param)
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

    val scene = Scene(outer, WIN_WIDTH, WIN_HEIGHT)
    scene.fill = Paint.valueOf(BACKGROUND)

    stage.scene = scene
    stage.show()
    renderToPngFile(scene, "/Users/kevinb9n/triangles.png")
  }

  private fun sign(alternate: Boolean, param: Int) =
    if (alternate) IntMath.pow(-1, param) else 1

  fun describe(d: Double, alternate: Boolean) = if (alternate) "$d*" else "$d"
}

data class Colors(val stroke: Paint, val fill: Paint) {
  constructor(stroke: String, fill:String) : this(Paint.valueOf(stroke), Paint.valueOf(fill))
  fun applyTo(node: Shape) {
    node.stroke = stroke
    node.fill = fill
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
