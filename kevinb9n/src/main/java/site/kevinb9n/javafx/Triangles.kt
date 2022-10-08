package site.kevinb9n.javafx

import com.google.common.base.Stopwatch
import javafx.application.Application
import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.format
import javafx.beans.binding.IntegerBinding
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableNumberValue
import javafx.collections.FXCollections
import javafx.geometry.BoundingBox
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.scene.shape.StrokeLineJoin
import javafx.stage.Stage
import site.kevinb9n.plane.Point
import kotlin.math.roundToInt

fun main() = Application.launch(Triangles::class.java)

const val MAX_SHAPE_INDEX = 252
val CHOOSABLE_SHAPES = 8..127
const val DEFAULT_SHAPES = 29

const val WIN_WIDTH = 1600
const val WIN_HEIGHT = 1000
const val MARGIN = 50
const val STROKE_WIDTH = 0.5

val COLORS = listOf(
  "001212", "c01613", "692d00", "ee8400", "ecc500", "139907", "0da73d", "00b179", "0ab7ad",
  "0869a3", "0e5faf", "1555bb", "223497", "20008d", "721a97", "c433a0", "e52383", "d10c2a",
).map { Color.web(it) }

val BACKGROUND = Color.web("d8cab2")

fun sliderFromArrayProperty(slider: Slider, label: Label, array: IntArray, defaultValue: Int):
  DoubleProperty {
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

  return SimpleDoubleProperty().apply {
    bind(lookupByIndexBinding(slider.valueProperty(), array))
    label.textProperty().bind(format("Shapes = %.0f", this))
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

    val controlPanel = VBox(10.0).also { it.prefWidth = 300.0 }
    val countControl = SliderWithReadout(Slider(), "Shapes", 1.0, "", """
      How many shapes would you like to see?
    """).also {
      it.valueProperty.unbind()
      it.valueProperty.bind(sliderFromArrayProperty(it.slider, it.label, selectableCounts(), DEFAULT_SHAPES))
      it.slider.isSnapToTicks = true
      controlPanel.children += it
    }

    val xControl = SliderWithReadout(Slider(-7.0, 7.0, snapRandom(3.0)), "Translate X", 0.005, "%.2f", """
      First and last shapes are separated by this distance in the "X" direction, and intervening
      shapes are linearly interpolated. Unit is the max shape height/width.
    """).also { controlPanel.children += it }

    val yControl = SliderWithReadout(Slider(-7.0, 7.0, snapRandom(3.0)), "Translate Y", 0.005, "%.2f", """
      First and last shapes are separated by this distance in the "Y" direction, and intervening
      shapes are linearly interpolated. Unit is the max shape height/width.
    """).also { controlPanel.children += it }

    val incrRotControl = SliderWithReadout(Slider(-180.0, 180.0, snapRandom(90)), "Rotation", 0.1, "%.0f", """
      The last shape is rotated at this angle relative to the first shape, and intervening shapes
      are linearly interpolated.
    """).also { controlPanel.children += it }

    val overallRotControl = SliderWithReadout(Slider(-180.0, 180.0, snapRandom(180)),
      "Overall rotation", 0.1, "%.0f", "Rotate the entire diagram").also { controlPanel.children += it }

    val opacityControl = SliderWithReadout(Slider(0.2, 5.0, 1.7), "Opacity", 0.05, "%.1f", """
      You'll want to make the shapes more transparent when they are highly overlapping.
    """).also { controlPanel.children += it }

    val strokeColor = toStrokeColor(color)

    val shapeList = (0..MAX_SHAPE_INDEX).map { shapeIndex ->
      shapeType.centeredPolygon(MAX_SHAPE_INDEX - shapeIndex, shapeIndex).apply {
        visibleProperty().bind(shapeVisibleProperty(countControl.valueProperty, shapeIndex))
        stroke = strokeColor
        strokeWidth = STROKE_WIDTH
        strokeLineJoin = StrokeLineJoin.ROUND

//        // Cancel the effects of scaling
//        strokeWidthProperty().bind(Bindings.divide(STROKE_WIDTH, autoScaleProp))

        val opacityProperty = opacityControl.valueProperty
        fillProperty().bindObject(countControl.valueProperty, opacityProperty) {
          color.opacityFactor(opacityProperty.value / countControl.valueProperty.value)
        }

        val unitOffset = shapeIndex - MAX_SHAPE_INDEX / 2.0
        translateXProperty().bind(xControl.valueProperty.multiply(unitOffset))
        translateYProperty().bind(yControl.valueProperty.multiply(unitOffset))

        val angleOffset = shapeIndex.toDouble() / MAX_SHAPE_INDEX - 0.5
        rotateProperty().bind(incrRotControl.valueProperty.multiply(angleOffset))

        id = "Shape " + shapeIndex
      }
    }

    val rotator = Group().apply {
      children += shapeList
      rotateProperty().bind(overallRotControl.valueProperty)
      id = "rotator"
    }

    val cropper = Pane(rotator).apply {
      id = "cropper"
      val boundsProp = rotator.boundsInParentProperty()
//      translateXProperty().bindDouble(boundsProp) { -rotator.boundsInParent.minX }
//      translateYProperty().bindDouble(boundsProp) { -rotator.boundsInParent.minY }
      prefWidthProperty().bindDouble(boundsProp) { rotator.boundsInParent.width }
      prefHeightProperty().bindDouble(boundsProp) { rotator.boundsInParent.height }
      // makeItClipNormally(this)
    }

    val scaler = createScalePane(cropper).apply {
      background = Background.fill(BACKGROUND)
      id = "scaler"
    }
    makeItClipNormally(scaler)

    val root = BorderPane().apply {
      center = scaler
      right = controlPanel
      controlPanel.viewOrder = 1.0 // ???
      id = "root"
    }

//    scaler.translateXProperty().bindDouble(rotater.boundsInParentProperty(), mainArea.boundsInParentProperty()) {
//      mainArea.boundsInParent.centerX - rotater.boundsInParent.centerX
//    }
//    scaler.translateYProperty().bindDouble(rotater.boundsInParentProperty(), mainArea.boundsInParentProperty()) {
//      mainArea.boundsInParent.centerY - rotater.boundsInParent.centerY
//    }


//    val autoScaleProp = SimpleDoubleProperty(1.0)
//    scaler.scaleXProperty().bind(autoScaleProp)
//    scaler.scaleYProperty().bind(autoScaleProp)
//    autoScaleProp.bindDouble(rotater.boundsInParentProperty(), mainArea.boundsInLocalProperty()) {
//       scaleToFit(rotater.boundsInParentProperty().get(), mainArea.boundsInLocalProperty().get())
//    }
//
//    controlPanel.children += Label().also {
//      it.textProperty().bind(format("Zoom = %.2f", autoScaleProp))
//    }

    val node: Node = shapeList[MAX_SHAPE_INDEX / 4]
    controlPanel.children += generateSequence(node) { it.parent }.map {
      Button(it.id ?: it.javaClass.simpleName).apply { setOnAction { _ -> dump(it) } }
    }

    val scene = Scene(root, WIN_WIDTH.toDouble(), WIN_HEIGHT.toDouble())

    stage.scene = scene
    stage.show()
  }

  private fun selectableCounts() = factors(MAX_SHAPE_INDEX)
    .map { it + 1 }
    .filter { it in CHOOSABLE_SHAPES }
    .toIntArray()

  private fun DoubleProperty.bindDouble(vararg dependencies: Observable, supplier: () -> Double) =
    bind(Bindings.createDoubleBinding(supplier, *dependencies))

  fun toStrokeColor(color: Color) =
    color.deriveColor(0.0, 10.0, 0.4, 200.0).opacityFactor(0.66)

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

class SliderWithReadout(
    val slider: Slider,
    name: String,
    fineTune: Double,
    format: String,
    tooltip: String = "") : VBox(6.0) {
  val valueProperty = SimpleDoubleProperty()
  val label = Label().also {
    it.textProperty().bind(format("$name = $format", valueProperty))
    it.tooltip = Tooltip(tooltip)
  }
  init {
    style = ("-fx-background-color: white;"
      + "-fx-border-color: black;"
      + "-fx-border-width: 1;"
      + "-fx-border-radius: 6;"
      + "-fx-padding: 8;")
    children += label
    children += slider

    slider.tooltip = Tooltip(tooltip)
    slider.blockIncrement = fineTune
    valueProperty.bind(slider.valueProperty())
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
