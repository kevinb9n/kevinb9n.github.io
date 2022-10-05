package site.kevinb9n.javafx

import com.google.common.base.Stopwatch
import javafx.application.Application
import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.format
import javafx.beans.binding.IntegerBinding
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ObservableNumberValue
import javafx.collections.FXCollections
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.scene.shape.StrokeLineJoin
import javafx.stage.Stage
import kotlin.math.min
import kotlin.math.roundToInt

fun main() = Application.launch(Triangles::class.java)

const val MAX_SHAPE_INDEX = 252
val CHOOSABLE_SHAPES = 8..127
const val DEFAULT_SHAPES = 29

const val WIN_WIDTH = 2000
const val WIN_HEIGHT = 1200
const val MARGIN = 50
const val STROKE_WIDTH = 0.5

val COLORS = listOf(
  "001212", "c01613", "692d00", "ee8400", "ecc500", "139907", "0da73d", "00b179", "0ab7ad",
  "0869a3", "0e5faf", "1555bb", "223497", "20008d", "721a97", "c433a0", "e52383", "d10c2a",
).map { Color.web(it) }

val BACKGROUND = Color.web("d8cab2")

fun sliderFromArrayProperty(slider: Slider, label: Label, array: IntArray, defaultValue: Int): IntegerProperty {
  // Has to be a double property, letting lookupByIndexBinding round() it, otherwise it gets floored
  val where = array.indexOf(defaultValue)
  require(where != -1)

  with(slider) {
    min = 0.0
    max = array.size - 1.0
    majorTickUnit = 1.0
    minorTickCount = 0
    value = where.toDouble()
  }

  return SimpleIntegerProperty().apply {
    bind(lookupByIndexBinding(slider.valueProperty(), array))
    label.textProperty().bind(format("%d", this))
  }
}

fun lookupByIndexBinding(source: ObservableNumberValue, array: IntArray): IntegerBinding {
  return object : IntegerBinding() {
    init { bind(source) }
    override fun computeValue() = array[source.doubleValue().roundToInt()]
    override fun getDependencies() = FXCollections.singletonObservableList(source)
    override fun dispose() = unbind(source)
  }
}

class Triangles : Application() {

  override fun start(stage: Stage) {
    val color = COLORS.random()
    val shapeType = ShapeType.values().random()

    val selectableCounts = factors(MAX_SHAPE_INDEX)
      .map { it + 1 }
      .filter { it in CHOOSABLE_SHAPES }
      .toIntArray()

    val countslider = Slider().apply {
      tooltip = Tooltip("Shape count")
      prefWidth = 300.0
      isShowTickMarks = true
      isSnapToTicks = true
      blockIncrement = 1.0
    }
    val countLabel = Label().apply { prefWidth = 40.0 }
    val shapeCountProperty = sliderFromArrayProperty(countslider, countLabel, selectableCounts, DEFAULT_SHAPES)

    val txslider = LabeledSlider(Slider(-7.0, 7.0, snapRandom(3.0)), "TranslateX", "%.2f")
    val tyslider = LabeledSlider(Slider(-7.0, 7.0, snapRandom(3.0)), "TranslateY", "%.2f")
    val rotslider = LabeledSlider(Slider(-180.0, 180.0, snapRandom(90)), "Rotation", "%.0f")
    val opacslider = LabeledSlider(Slider(0.2, 5.0, 1.7), "Opacity", "%.1f")

    txslider.slider.blockIncrement = 0.002
    tyslider.slider.blockIncrement = 0.002
    rotslider.slider.blockIncrement = 0.2
    opacslider.slider.blockIncrement = 0.05

    val strokeColor = toStrokeColor(color)

    val thePolygons = (0..MAX_SHAPE_INDEX).map { shapeIndex ->
      shapeType.centeredPolygon(MAX_SHAPE_INDEX - shapeIndex, shapeIndex).apply {
        visibleProperty().bind(shapeVisibleProperty(shapeCountProperty, shapeIndex))
        stroke = strokeColor
        strokeWidth = STROKE_WIDTH
        strokeLineJoin = StrokeLineJoin.ROUND

//        // Cancel the effects of scaling
//        strokeWidthProperty().bind(Bindings.divide(STROKE_WIDTH, autoScaleProp))

        val opacityProperty = opacslider.valueProperty()
        fillProperty().bindObject(shapeCountProperty, opacityProperty) {
          color.opacityFactor(opacityProperty.value / shapeCountProperty.value)
        }

        val unitOffset = shapeIndex - MAX_SHAPE_INDEX / 2.0
        translateXProperty().bind(txslider.valueProperty().multiply(unitOffset))
        translateYProperty().bind(tyslider.valueProperty().multiply(unitOffset))

        val angleOffset = shapeIndex.toDouble() / MAX_SHAPE_INDEX - 0.5
        rotateProperty().bind(rotslider.valueProperty().multiply(angleOffset))
      }
    }

    val rotater = Pane()
    rotater.children += thePolygons
    rotater.rotateProperty().bindDouble(rotater.boundsInLocalProperty()) {
      printBounds("rotater", rotater.boundsInLocal)
//      println("old rotate: ${rotater.rotate}")
      val d = findBestRotation(thePolygons)
      println("new rotate: $d")
      d
    }

    val scaler = Pane(rotater)
    val autoScaleProp = SimpleDoubleProperty(1.0)
    scaler.scaleXProperty().bind(autoScaleProp)
    scaler.scaleYProperty().bind(autoScaleProp)
    autoScaleProp.bindDouble(scaler.boundsInLocalProperty()) {
      printBounds("scaler", scaler.boundsInLocal)
//      println("old scale: ${scaler.scaleX}")
      val d = scaleToFit(scaler.boundsInLocal)
      println("new scale: $d")
      d
    }

    val translater = Pane(scaler)
    translater.translateXProperty().bindDouble(translater.boundsInLocalProperty()) {
      printBounds("translater", translater.boundsInLocal)
//      println("old translateX: ${translater.translateX}")
      val d = USABLE.centerX - translater.boundsInLocal.centerX
      println("new translateX: $d")
      d
    }
    translater.translateYProperty().bindDouble(translater.boundsInLocalProperty()) {
      printBounds("translater", translater.boundsInLocal)
//      println("old translateY: ${translater.translateY}")
      val d = USABLE.centerY - translater.boundsInLocal.centerY
      println("new translateY: $d")
      d
    }

    val zoomLabel = Label()
    zoomLabel.textProperty().bind(format("%.2f", autoScaleProp))

    val sceneRoot = BorderPane()
    val pretty = Pane(translater).apply { background = Background.fill(BACKGROUND) }
    sceneRoot.center = pretty

    sceneRoot.bottom = HBox(10.0).apply {
      children += listOf(
        countslider, countLabel,
        txslider.slider, txslider.label,
        tyslider.slider, tyslider.label,
        rotslider.slider, rotslider.label,
        opacslider.slider, opacslider.label,
        zoomLabel)
    }

    val scene = Scene(sceneRoot, WIN_WIDTH.toDouble(), WIN_HEIGHT.toDouble())

    stage.scene = scene
    stage.show()
    // renderToPngFile(pretty, "/Users/kevinb9n/triangles.png")
  }

  private fun DoubleProperty.bindDouble(vararg dependencies: Observable, supplier: () -> Double) =
    bind(Bindings.createDoubleBinding(supplier, *dependencies))

  fun toStrokeColor(color: Color) =
    color.deriveColor(0.0, 10.0, 0.4, 200.0).opacityFactor(0.66)

}
val USABLE = box(Point(MARGIN, MARGIN), Point(WIN_WIDTH - MARGIN, WIN_HEIGHT - MARGIN * 2))

fun scaleToFit(bound: Bounds): Double {
  return (min(USABLE.width / bound.width, USABLE.height / bound.height) * 100.0).roundToInt() /
    100.0
}

fun findBestRotation(polygons: List<Polygon>): Double {
  val stopwatch = Stopwatch.createStarted()
  val neverShown = Group()
  neverShown.children += polygons.map(::clone)

  // It would be fun to find a better algorithm for this
  var best = findBestOf(0 until 180 step 30, neverShown) // 6
  best = findBestOf(best - 25 .. best + 25 step 10, neverShown) // 6
  best = findBestOf(best - 7 .. best + 8 step 3, neverShown) // 6
  best = findBestOf(best - 2 .. best + 2, neverShown) // 5
  println(stopwatch)
  println("angle: $best")
  return best.toDouble()
}

private fun clone(it: Polygon) = Polygon().apply {
  this.points.addAll(it.points)
  translateX = it.translateX
  translateY = it.translateY
  rotate = it.rotate
  scaleX = it.scaleX
  scaleY = it.scaleY
//  strokeWidth = it.strokeWidth
//  strokeLineJoin = it.strokeLineJoin
//  strokeType = it.strokeType
//  stroke = it.stroke
//  require(boundsInLocal == it.boundsInLocal) { "\n$boundsInLocal\n${it.boundsInLocal}" }
}

fun findBestOf(anglesToTry: IntProgression, neverShown: Group) = anglesToTry.minByOrNull {
  neverShown.rotate = it.toDouble()
  neverShown.boundsInParent.height
}!!

class LabeledSlider(val slider: Slider, tooltip: String, format: String) {
  val label = labelFor(slider, format)

  init {
    slider.tooltip = Tooltip(tooltip)
    slider.prefWidth = 300.0
  }

  fun valueProperty() = slider.valueProperty()

  private fun labelFor(control: Slider, format: String) =
    Label().apply {
      prefWidth = 40.0
      textProperty().bind(format(format, control.valueProperty()))
    }
}

enum class ShapeType(val points: List<Point>) {
  ISOS_TRI(listOf(Point(0, 1), Point(1, 1), Point(0.5, 0))),
  RIGHT_TRI(listOf(Point(0, 1), Point(1, 1), Point(0, 0))),
  RECTANGLE(listOf(Point(0, 1), Point(1, 1), Point(1, 0), Point(0, 0))),
  ISOS_TRAP(listOf(Point(0, 1), Point(1, 1), Point(0.75, 0), Point(0.25, 0))),
  RHOMBUS(listOf(Point(0.5, 1), Point(1, 0.5), Point(0.5, 0), Point(0, 0.5))),
  PGRAM(listOf(Point(0, 1), Point(0.5, 1), Point(1, 0), Point(0.5, 0))),
  ;

  fun centeredPolygon(width: Number, height: Number): Polygon {
    val widthd = width.toDouble()
    val heightd = height.toDouble()
    val realPoints = points.map { Point((it.x - 0.5) * widthd, (it.y - 0.5) * heightd) }
    val polygon = pointsToPolygon(realPoints)
    require(polygon.boundsInLocal == BoundingBox(-widthd / 2, -heightd / 2, widthd, heightd))
    return polygon
  }
}
