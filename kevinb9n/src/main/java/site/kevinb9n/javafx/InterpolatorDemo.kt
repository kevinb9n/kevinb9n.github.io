package site.kevinb9n.javafx

import com.google.common.math.LinearTransformation
import javafx.application.Application
import javafx.beans.value.ChangeListener
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polyline
import javafx.stage.Stage
import site.kevinb9n.math.MonotoneCubicInterpolator
import java.lang.Math.pow
import java.util.function.DoubleUnaryOperator
import kotlin.math.ln

fun main() = Application.launch(InterpolatorDemo::class.java)

class InterpolatorDemo : Application() {
  override fun start(stage: Stage) {
    val start = Circle(100.0, 700.0, 8.0).apply {
      fill = Color.DARKBLUE
    }
    val a = Circle(300.0, 500.0, 8.0).apply {
      fill = Color.DARKBLUE
    }
//    val b = Circle(500.0, 300.0, 8.0).apply {
//      fill = Color.DARKGREEN
//    }
    val end = Circle(700.0, 100.0, 8.0).apply {
      fill = Color.DARKBLUE
    }
    val circles = listOf(start, a, end)

    val powerline = Polyline()
    powerline.strokeWidth = 7.0
    powerline.stroke = Color.DARKGREEN
    val cubicline = Polyline()
    cubicline.strokeWidth = 4.0
    cubicline.stroke = Color.MEDIUMVIOLETRED
    replacePoints(circles, powerline, cubicline)

    val cl = ChangeListener<Any> { _, _, _ -> replacePoints(circles, powerline, cubicline) }
    circles.forEach { it.boundsInParentProperty().addListener(cl) }

    val rootPane = Pane(
      powerline,
      cubicline,
      start,
      DragToTranslate(a),
//      DragToTranslate(b),
      end,
    )

    with(stage) {
      scene = Scene(rootPane, 800.0, 800.0)
      title = "Interpolator Demo"
      show()
    }
  }

  fun replacePoints(circles: List<Circle>, powerline: Polyline, cubicline: Polyline) {
    val xs = circles.map { it.boundsInParent.centerX }.toDoubleArray()
    val ys = circles.map { it.boundsInParent.centerY }.toDoubleArray()
    val interp1 = MonotoneCubicInterpolator.of(xs, ys)
    val cubicpath = (100.0..700.0 step 2.0).flatMap { listOf(it, interp1.applyAsDouble(it)) }
    cubicline.points.clear()
    cubicline.points += cubicpath

    val interp2 = simpleInterpolator(xs, ys)
    val powerpath = (100.0..700.0 step 2.0).flatMap { listOf(it, interp2.applyAsDouble(it)) }
    powerline.points.clear()
    powerline.points += powerpath
  }

  fun simpleInterpolator(xs: DoubleArray, ys: DoubleArray): DoubleUnaryOperator {
    val xtrans = LinearTransformation.mapping(xs[0], 0.0).and(xs[2], 1.0)
    val ytrans = LinearTransformation.mapping(ys[0], 0.0).and(ys[2], 1.0)
    val rytrans = ytrans.inverse()
    val xprop = xtrans.transform(xs[1])
    val yprop = ytrans.transform(ys[1])
    val exp = ln(yprop) / ln(xprop)
    return DoubleUnaryOperator { rytrans.transform(pow(xtrans.transform(it), exp)) }
  }

  infix fun ClosedRange<Double>.step(step: Double): Sequence<Double> =
    generateSequence(this.start) { it + step }.takeWhile { it in this }
}
