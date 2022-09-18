package site.kevinb9n.javafx

import com.google.common.math.IntMath
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Paint
import javafx.scene.shape.Polygon
import javafx.scene.shape.Shape
import javafx.scene.shape.StrokeLineJoin
import javafx.stage.Stage
import java.lang.Math.random
import java.nio.file.Files
import java.nio.file.Path

fun main() = Application.launch(Triangles::class.java)

data class Colors(val stroke: Paint, val fill: Paint) {
  constructor(s: String, f:String) : this(Paint.valueOf(s), Paint.valueOf(f))
  fun applyTo(node: Shape) {
    node.stroke = stroke
    node.fill = fill
  }
}

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
  RECTANGLE {
    override fun centeredPolygon(base: Double, height: Double) =
      polygon(
        Point(-base / 2, -height / 2),
        Point(base / 2, -height / 2),
        Point(base / 2, height / 2),
        Point(-base / 2, height / 2))
  },
  ;

  abstract fun centeredPolygon(base: Double, height: Double): Polygon
}

class Triangles : Application() {
  val WIN_WIDTH = 2000.0
  val WIN_HEIGHT = 1200.0
  val MARGIN = 50.0
  val USABLE = box(Point(MARGIN, MARGIN), Point(WIN_WIDTH - MARGIN, WIN_HEIGHT - MARGIN))

  val REAL_STROKE = 1.0

  val COLORSES = listOf(
    Colors("#550011", "#aa002207"),
    Colors("#274e13", "#2dc35405"),
    Colors("#001155", "#0033aa07"),
    Colors("#29133f", "#53277e08"))

  override fun start(stage: Stage) {
    val offsetX = snapRandom(1.333)
    val offsetY = snapRandom(1.333)
    val rotation = snapRandom(90 / 56.0) // ?
    val startRot = snapRandom(180)

    val altXOffset = random() < 0.25
    val altYOffset = random() < 0.25
    val altRotate = random() < 0.25

    val xdesc = describe(offsetX, altXOffset)
    val ydesc = describe(offsetY, altYOffset)
    val rotdesc = describe(rotation, altRotate)

    val colors = COLORSES.random()
    val shapeType = ShapeType.values().random()

    val path = Path.of("/Users/kevinb9n/triangles.txt")
    val desc = "$shapeType, offset ($xdesc, $ydesc), rotation start $startRot, incr $rotdesc\n"
    println(desc)
    Files.writeString(path, desc)

    val triangles = (0..56).map { param ->
      val base = 56.0 - param
      val height = 0.0 + param

      val p = shapeType.centeredPolygon(base, height)
      colors.applyTo(p)
      p.strokeLineJoin = StrokeLineJoin.ROUND
      p.strokeWidth = .05 // temporary

      p.translateX = param * offsetX * sign(altXOffset, param)
      p.translateY = param * offsetY * sign(altYOffset, param)
      p.rotate = startRot + param * rotation * sign(altRotate, param)
      p
    }
    val stack = Group()
    stack.children += triangles
    printBounds("start", stack)

    // Why not just brute force it? And yes it's weird that we're mutating.
    val goodAngle = (0..170 step 10).minByOrNull {
      stack.rotate = it.toDouble()
      stack.boundsInParent.height
    }!!
    val bestAngle = ((goodAngle - 9)..(goodAngle + 9)).minByOrNull {
      stack.rotate = it.toDouble()
      stack.boundsInParent.height
    }!!

    stack.rotate = bestAngle.toDouble()
    printBounds("after rotate by $bestAngle", stack)
    val outer = Group()
    outer.children += stack
    printBounds("outer", outer)

    var scale = scaleToFit(outer.boundsInLocal, USABLE)
    stack.children.filterIsInstance(Shape::class.java).forEach {
      it.strokeWidth = REAL_STROKE / scale
    }
    scale = scaleToFit(outer.boundsInLocal, USABLE)
    outer.scaleX = scale
    outer.scaleY = scale
    printBounds("after scale by ${round(scale)}", outer)

    outer.translateX = USABLE.centerX - outer.boundsInParent.centerX
    outer.translateY = USABLE.centerY - outer.boundsInParent.centerY
    printBounds("after translate by (${round(outer.translateX)}, ${round(outer.translateY)})", outer)

    val scene = Scene(outer, WIN_WIDTH, WIN_HEIGHT)
    scene.fill = Paint.valueOf("#d8cab2")

    stage.scene = scene
    stage.show()
    renderToPngFile(scene, "/Users/kevinb9n/triangles.png")
  }

  private fun sign(alternate: Boolean, param: Int) =
    if (alternate) IntMath.pow(-1, param) else 1

  fun describe(d: Double, alternate: Boolean) = if (alternate) "$d*" else "$d"
}