package site.kevinb9n.javafx

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ObservableDoubleValue
import javafx.collections.FXCollections.singletonObservableList
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Polygon
import javafx.scene.shape.Shape
import javafx.scene.shape.StrokeLineJoin
import javafx.stage.Stage
import javafx.util.StringConverter
import javafx.util.converter.IntegerStringConverter
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin

fun main() = Application.launch(Triangles::class.java)

val SHAPE_COUNT = 252

class Triangles : Application() {
  val WIN_WIDTH = 2000.0
  val WIN_HEIGHT = 1200.0
  val MARGIN = 200.0
  val REAL_STROKE = 0.9

  val BACKGROUND = "#d8cab2"
  val COLORS = listOf(
    "001212", // gray
    "c01613", // red
    "692d00", // brown
    "ee8400", // orange
    "ecc500", // yellow
    "b2ce00", // yellow-green, bleh
    "139907", // green
    "0da73d", // kelly green
    "00b179", // turquoise
    "0ab7ad", // aqua
    "0869a3", // cool soft aqueous
    "0e5faf", // soft blue
    "1555bb", // perfect blue
    "223497", // indigo
    "20008d", // purple
    "721a97", // purple
    "c433a0", // magenta
    "e52383", // pink
    "d10c2a", // reddy red
  ).map { Color.web(it) }

  val USABLE = box(Point(MARGIN, MARGIN), Point(WIN_WIDTH - MARGIN, WIN_HEIGHT - MARGIN * 2))

  fun Color.opaquenessFactor(factor: Double) = this.deriveColor(0.0, 1.0, 1.0, factor)

  override fun start(stage: Stage) {
    val offsetDistance = snapRandom(2.0)
    val offsetAngle = snapRandom(180.0)
    val offsetX = round(offsetDistance * cos(toRadians(offsetAngle)))
    val offsetY = round(offsetDistance * sin(toRadians(offsetAngle)))

    val rotation = round(snapRandom(90.0))

    val color = COLORS.random()
    val shapeType = ShapeType.values().random()

    val counts = arrayOf(13, 15, 19, 22, 29, 37, 43, 64, 85)
    val countslider = Slider(0.0, counts.size.toDouble(), counts.size / 2.0)

    val shapeCountProperty = SimpleIntegerProperty()
    val countDisplay = Label()
    Bindings.bindBidirectional(countDisplay.textProperty(), shapeCountProperty,
      IntegerStringConverter() as StringConverter<Number>)
    shapeCountProperty.bind(arrayLookupBinding(countslider.valueProperty(), counts))

    val txslider = Slider(-500.0, 500.0, offsetX * SHAPE_COUNT).apply {
      tooltip = Tooltip("Translate X")
    }
    val tyslider = Slider(-500.0, 500.0, offsetY * SHAPE_COUNT).apply {
      tooltip = Tooltip("Translate Y")
    }
    val rotslider = Slider(-180.0, 180.0, rotation).apply {
      tooltip = Tooltip("Translate Y")
    }
    val opacslider = Slider(0.2, 5.0, 1.7).apply {
      tooltip = Tooltip("Opacity")
    }

    val triangles = (0 .. SHAPE_COUNT).map { param ->
      val height = param.toDouble()
      val base = SHAPE_COUNT - height
      shapeType.centeredPolygon(base, height).apply {
        visibleProperty().bind(shapeVisible(shapeCountProperty, param))

        stroke = color.deriveColor(0.0, 10.0, 0.4, 200.0).opaquenessFactor(0.66)

        val opacprop = opacslider.valueProperty()
        fillProperty().bind(Bindings.createObjectBinding({
          color.opaquenessFactor(opacprop.value / shapeCountProperty.value)}, shapeCountProperty, opacprop))

        strokeLineJoin = StrokeLineJoin.ROUND
        strokeWidth = .05 // temporary

        translateXProperty().bind(txslider.valueProperty().multiply(height / SHAPE_COUNT))
        translateYProperty().bind(tyslider.valueProperty().multiply(height / SHAPE_COUNT))
        rotateProperty().bind(rotslider.valueProperty().multiply(height / SHAPE_COUNT))
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
    sceneRoot.bottom = HBox(6.0).apply {
      children += listOf(countslider, countDisplay, txslider, tyslider, rotslider, opacslider)
    }

    val scene = Scene(sceneRoot, WIN_WIDTH, WIN_HEIGHT)

    stage.scene = scene
    stage.show()
    // renderToPngFile(pretty, "/Users/kevinb9n/triangles.png")
  }
}

data class ParametricShapeSet(
  val shapeType: ShapeType,
  val count: Int,
  val axis: Point2D, // point to rotate around
)

enum class ShapeType {
  ISOSCELES {
    override fun polygon(base: Double, height: Double) =
      arrayOf(Point(0, 0), Point(base, 0), Point(base / 2, -height))
  },
  RIGHT {
    override fun polygon(base: Double, height: Double) =
      arrayOf(Point(0, 0), Point(base, 0), Point(0, -height))
  },
  RECTANGLE {
    override fun polygon(base: Double, height: Double) =
      arrayOf(Point(0, 0), Point(base, 0), Point(base, height), Point(0, height))
  },
  ISOS_TRAP {
    override fun polygon(base: Double, height: Double) =
      arrayOf(Point(0, height), Point(base, height), Point(base * 3/4, 0), Point(base / 4, 0))
  },
  DIAMOND {
    override fun polygon(base: Double, height: Double) =
      arrayOf(Point(0, 0), Point(base/2, height/2), Point(base, 0), Point(base/2, -height/2))
  },
  PGRAM {
    override fun polygon(base: Double, height: Double) =
      arrayOf(Point(base / 2, 0), Point(base, 0), Point(base / 2, height), Point(0, height))
  },
  ;

  abstract fun polygon(base: Double, height: Double): Array<Point>

  fun centeredPolygon(base: Double, height: Double): Polygon {
    val points = polygon(base, height)
    val xs = points.map { it.x }
    val ys = points.map { it.y }
    val centerx = mean(xs.minOrNull()!!, xs.maxOrNull()!!)
    val centery = mean(ys.minOrNull()!!, ys.maxOrNull()!!)
    return polygon(points.map { Point(it.x - centerx, it.y - centery) })
  }

  fun mean(a: Double, b: Double) = (a + b) / 2.0
}

fun arrayLookupBinding(source: ObservableDoubleValue, array: Array<Int>): IntegerBinding {
  return object : IntegerBinding() {
    init { bind(source) }
    override fun computeValue() = array[min(floor(source.doubleValue()).toInt(), array.size - 1)]
    override fun getDependencies() = singletonObservableList(source)
    override fun dispose() = unbind(source)
  }
}

fun shapeVisible(source: IntegerProperty, shapeNum: Int): BooleanBinding {
  return object : BooleanBinding() {
    init { bind(source) }
    override fun computeValue() :Boolean {
      return (source.value - 1) * shapeNum % SHAPE_COUNT == 0
    }
    override fun getDependencies() = singletonObservableList(source)
    override fun dispose() = unbind(source)
  }
}
