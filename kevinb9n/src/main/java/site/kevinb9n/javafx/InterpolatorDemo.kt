package site.kevinb9n.javafx

import com.google.common.math.LinearTransformation
import javafx.animation.Interpolator
import javafx.application.Application
import javafx.beans.value.ChangeListener
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polyline
import javafx.stage.Stage
import site.kevinb9n.math.MonotoneCubicInterpolator
import site.kevinb9n.plane.Point2
import java.lang.Math.pow
import kotlin.math.ln

fun main() = Application.launch(InterpolatorDemo::class.java)

class InterpolatorDemo : Application() {
  override fun start(stage: Stage) {
    val start = Circle(100.0, 700.0, 8.0).apply {
      fill = Color.DARKBLUE
    }
    val a = Circle(400.0, 400.0, 8.0).apply {
      fill = Color.DARKBLUE
    }
    val end = Circle(700.0, 100.0, 8.0).apply {
      fill = Color.DARKBLUE
    }
    val circles = listOf(start, a, end)

    val powerline = Polyline().apply {
      strokeWidth = 6.0
      stroke = Color.MEDIUMVIOLETRED
    }
    val cubicline = Polyline().apply {
      strokeWidth = 5.0
      stroke = Color.DARKGREEN
    }
    val splineline = Polyline().apply {
      strokeWidth = 4.0
      stroke = Color.DARKORANGE
    }
    replacePoints(circles, powerline, cubicline, splineline)

    val cl = ChangeListener<Any> { _, _, _ -> replacePoints(circles, powerline, cubicline, splineline) }
    circles.forEach { it.boundsInParentProperty().addListener(cl) }

    val rootPane = Pane(
      powerline,
      cubicline,
      splineline,
      start,
      DragToTranslate(a),
      end,
    )

    with(stage) {
      scene = Scene(rootPane, 800.0, 800.0)
      title = "Interpolator Demo"
      show()
    }
  }

  interface Interpolation {
    fun forPoints(points: List<Point2>): (Double) -> Double
  }

  object JoshInterpolation : Interpolation {
    override fun forPoints(points: List<Point2>): (Double) -> Double {
      val fn = MonotoneCubicInterpolator.of(
        points.map { it.x }.toDoubleArray(),
        points.map { it.y }.toDoubleArray())
      return { fn.applyAsDouble(it) }
    }
  }

  object JavafxSplineInterpolation : Interpolation {
    fun byEagerness(eagerness: Double): (Double) -> Double {
      return forPoints(listOf(Point2(0, 0), Point2(1 - eagerness, eagerness), Point2(1, 1)))
    }

    override fun forPoints(points: List<Point2>): (Double) -> Double {
      require(points.size == 3)
      val sorted = points.sortedBy(Point2::x)
      val first = sorted.first()
      val last = sorted.last()
      val firstY = first.y
      val lastY = last.y

      val xToUnit = LinearTransformation.mapping(first.x, 0.0).and(last.x, 1.0)
      val yToUnit = LinearTransformation.mapping(firstY, 0.0).and(lastY, 1.0)
      fun normalize(p: Point2) = Point2(xToUnit.transform(p.x), yToUnit.transform(p.y))

      require(normalize(points[0]) == Point2(0.0, 0.0))
      require(normalize(points[2]) == Point2(1.0, 1.0))
      val a = normalize(points[1])
      val spline = Interpolator.SPLINE(a.x, a.y, a.x, a.y)

      return { spline.interpolate(firstY, lastY, xToUnit.transform(it)) }
    }
  }

  object PowerInterpolation : Interpolation {
    override fun forPoints(points: List<Point2>): (Double) -> Double {
      require(points.size == 3)
      val sorted = points.sortedBy(Point2::x)
      val first = sorted.first()
      val last = sorted.last()
      val firstY = first.y
      val lastY = last.y

      val xToUnit = LinearTransformation.mapping(first.x, 0.0).and(last.x, 1.0)
      val yToUnit = LinearTransformation.mapping(firstY, 0.0).and(lastY, 1.0)
      fun normalize(p: Point2) = Point2(xToUnit.transform(p.x), yToUnit.transform(p.y))

      require(normalize(points[0]) == Point2(0.0, 0.0))
      require(normalize(points[2]) == Point2(1.0, 1.0))
      val a = normalize(points[1])
      val exp = ln(a.y) / ln(a.x)
      val spline = object : Interpolator() {
        override fun curve(p0: Double) = pow(p0, exp)
      }
      return { spline.interpolate(firstY, lastY, xToUnit.transform(it)) }
    }
  }

  fun replacePoints(circles: List<Circle>, powerline: Polyline, cubicline: Polyline, splineline: Polyline) {
    val pts = circles.map { Point2(it.boundsInParent.centerX, it.boundsInParent.centerY) }
    val sequence = 100.0..700.0 step 4.0

    val interp1 = JoshInterpolation.forPoints(pts)
    val cubicpath = sequence.flatMap { listOf(it, interp1(it)) }
    cubicline.points.clear()
    cubicline.points += cubicpath

    val interp2 = PowerInterpolation.forPoints(pts)
    val powerpath = sequence.flatMap {
      require(it > 0.0 && it < 800.0) { it }
      val result = interp2(it)
      require(result > 0.0 && result < 800.0) { result }
      listOf(it, result)
    }
    powerline.points.clear()
    powerline.points += powerpath

    val interp3 = JavafxSplineInterpolation.forPoints(pts)
    val splinepath = sequence.flatMap { listOf(it, interp3(it)) }
    splineline.points.clear()
    splineline.points += splinepath
  }

  infix fun ClosedRange<Double>.step(step: Double): Sequence<Double> =
    generateSequence(this.start) { it + step }.takeWhile { it in this }
}
