package site.kevinb9n.javafx

import com.google.common.base.Stopwatch
import javafx.application.Application
import javafx.beans.binding.Bindings.format
import javafx.beans.property.SimpleDoubleProperty
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
import site.kevinb9n.javafx.InterpolatorDemo.JavafxSplineInterpolation
import site.kevinb9n.math.factors
import site.kevinb9n.plane.Angle
import site.kevinb9n.plane.Point2D

fun main() = Application.launch(Triangles::class.java)

const val MAX_SHAPE_INDEX = 252
val CHOOSABLE_SHAPES = 8..100
const val DEFAULT_SHAPE_COUNT = 29

const val INITIAL_WIN_WIDTH = 1300
const val INITIAL_WIN_HEIGHT = 900

val COLORS = listOf(
  "001212", "c01613", "692d00", "ee8400", "ecc500", "139907", "0da73d", "00b179", "0ab7ad",
  "0869a3", "0e5faf", "1555bb", "223497", "20008d", "721a97", "c433a0", "e52383", "d10c2a",
).map { Color.web(it) }

val BACKGROUND = Color.web("d8cab2")

class Triangles : Application() {

  override fun start(stage: Stage) {
    // defaults
    val shapeType = ShapeType.values().random()
    val color = COLORS.random()
    val strokeColor = toStrokeColor(color)

    val controlPanel = VBox(10.0).also { it.prefWidth = 270.0 }
    val countControl = SliderWithReadout(Slider(), "Shapes", 1.0, "", """
      How many shapes would you like to see?
    """).also {
      it.valueProperty.unbind()
      it.valueProperty.bind(sliderFromArrayProperty(it.slider, it.label, selectableCounts(), DEFAULT_SHAPE_COUNT, "Shapes = %.0f"))
      it.slider.isSnapToTicks = true
      controlPanel.children += it
    }

    val onAxisControl = SliderWithReadout(Slider(-7.0, 7.0, 0.0), "Offset on-axis", 0.005, "%.2f", """
      First and last shapes are separated by this distance in the "X" direction, and intervening
      shapes are linearly interpolated. Unit is the max shape height/width.
    """).also { controlPanel.children += it }

    val onAxisBunchyControl = SliderWithReadout(Slider(0.1, 0.9, 0.5), "On axis bunchiness", 0.01, "%.02f", """
      get bunchy
    """).also { controlPanel.children += it }

    val offAxisControl = SliderWithReadout(Slider(-7.0, 7.0, 0.0), "Offset off-axis", 0.005, "%.2f", """
      First and last shapes are separated by this distance in the "Y" direction, and intervening
      shapes are linearly interpolated. Unit is the max shape height/width.
    """).also { controlPanel.children += it }

    val axisControl = SliderWithReadout(Slider(-200.0, 200.0, 0.0), "Axis for offset", 0.2, "%.0f", """
      The last shape is rotated at this angle relative to the first shape, and intervening shapes
      are linearly interpolated.
    """).also { controlPanel.children += it }

    val incrRotControl = SliderWithReadout(Slider(-200.0, 200.0, 0.0), "Rotation increment", 0.2, "%.0f", """
      The last shape is rotated at this angle relative to the first shape, and intervening shapes
      are linearly interpolated.
    """).also { controlPanel.children += it }

    val rotBunchControl = SliderWithReadout(Slider(0.1, 0.9, 0.5), "Rotation bunchiness", 0.01, "%.02f", """
      get bunchy
    """).also { controlPanel.children += it }

    val overallXControl = SliderWithReadout(Slider(0.0, 2000.0, 800.0),
      "Overall Translate X", 1.0, "%.0f", "Horizontally position the diagram").also {
      controlPanel.children += it }

    val overallYControl = SliderWithReadout(Slider(0.0, 2000.0, 600.0),
      "Overall Translate Y", 1.0, "%.0f", "Vertically position the diagram").also { controlPanel
      .children += it }

    val overallRotControl = SliderWithReadout(Slider(-225.0, 225.0, 0.0),
      "Overall rotation", 0.5, "%.0f", "Rotate the entire diagram").also { controlPanel.children += it }

    val overallScaleControl = SliderWithReadout(Slider(0.1, 20.0, 3.0),
      "Overall scale", 0.1, "%.2f", "Scale the entire diagram").also { controlPanel.children += it }

    val opacityControl = SliderWithReadout(Slider(0.2, 5.0, 1.7), "Opacity", 0.05, "%.1f", """
      You'll want to make the shapes more transparent when they are highly overlapping.
    """).also { controlPanel.children += it }


    val rotator = Group()
    val shapeList = (0..MAX_SHAPE_INDEX).map { shapeIndex ->
      shapeType.centeredPolygon(MAX_SHAPE_INDEX - shapeIndex, shapeIndex).apply {
        id = "Shape " + shapeIndex
        stroke = strokeColor
        strokeLineJoin = StrokeLineJoin.ROUND

        visibleProperty().bind(shapeVisibleProperty(countControl.valueProperty, shapeIndex))

        strokeWidthProperty().bindDouble(rotator.localToSceneTransformProperty(), countControl.valueProperty) {
          180 / (50 + countControl.valueProperty.get()) / (5 + rotator.scaleY)
        }

        val opacityProperty = opacityControl.valueProperty
        fillProperty().bindObject(countControl.valueProperty, opacityProperty) {
          color.opacityFactor(opacityProperty.value / countControl.valueProperty.value)
        }

        val fuck = shapeIndex.toDouble() / MAX_SHAPE_INDEX
        val unadjustedfuck = MAX_SHAPE_INDEX * (fuck - 0.5)
        translateXProperty().bindDouble(onAxisControl.valueProperty, offAxisControl.valueProperty, axisControl.valueProperty, onAxisBunchyControl.valueProperty) {
          val fkn = JavafxSplineInterpolation.byEagerness(onAxisBunchyControl.valueProperty.get())
          val adjustedfuck = MAX_SHAPE_INDEX * (fkn(fuck) - 0.5)
          val d = adjustedfuck * onAxisControl.valueProperty.get()
          val axis = Angle.degrees(axisControl.valueProperty.get())
          val d1 = unadjustedfuck * offAxisControl.valueProperty.get()
          d * axis.cos() - d1 * axis.sin()
        }
        translateYProperty().bindDouble(onAxisControl.valueProperty, offAxisControl.valueProperty, axisControl.valueProperty, onAxisBunchyControl.valueProperty) {
          val fkn = JavafxSplineInterpolation.byEagerness(onAxisBunchyControl.valueProperty.get())
          val adjustedfuck = MAX_SHAPE_INDEX * (fkn(fuck) - 0.5)
          val d = adjustedfuck * onAxisControl.valueProperty.get()
          val axis = Angle.degrees(axisControl.valueProperty.get())
          val d1 = unadjustedfuck * offAxisControl.valueProperty.get()
          d * axis.sin() + d1 * axis.cos()
        }

        rotateProperty().bindDouble(incrRotControl.valueProperty, rotBunchControl.valueProperty) {
          val normal = shapeIndex.toDouble() / MAX_SHAPE_INDEX
          val fn = JavafxSplineInterpolation.byEagerness(rotBunchControl.valueProperty.get())
          val adj = fn(normal) - 0.5
          incrRotControl.valueProperty.get() * adj
        }

      }
    }

    rotator.apply {
      children += shapeList
      rotateProperty().bind(overallRotControl.valueProperty)
      scaleXProperty().bind(overallScaleControl.valueProperty)
      scaleYProperty().bind(overallScaleControl.valueProperty)
      id = "rotator"
    }

    val cropper = Pane(rotator).apply {
      id = "cropper"
      translateXProperty().bind(overallXControl.valueProperty)
      translateYProperty().bind(overallYControl.valueProperty)
//      prefWidthProperty().bindDouble(boundsProp) { rotator.boundsInParent.width }
//      prefHeightProperty().bindDouble(boundsProp) { rotator.boundsInParent.height }
    }

    val scaler = Pane(cropper).apply {
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

    val scene = Scene(root, INITIAL_WIN_WIDTH.toDouble(), INITIAL_WIN_HEIGHT.toDouble())

    stage.scene = scene
    stage.show()
  }

  private fun selectableCounts() = factors(MAX_SHAPE_INDEX)
    .map { it + 1 }
    .filter { it in CHOOSABLE_SHAPES }
    .toIntArray()

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

enum class ShapeType(val points: List<Point2D>) {
  ISOS_TRI(listOf(Point2D(0, 1), Point2D(1, 1), Point2D(0.5, 0))),
  RIGHT_TRI(listOf(Point2D(0, 1), Point2D(1, 1), Point2D(0, 0))),
  RECTANGLE(listOf(Point2D(0, 1), Point2D(1, 1), Point2D(1, 0), Point2D(0, 0))),
  ISOS_TRAP(listOf(Point2D(0, 1), Point2D(1, 1), Point2D(0.75, 0), Point2D(0.25, 0))),
  RHOMBUS(listOf(Point2D(0.5, 1), Point2D(1, 0.5), Point2D(0.5, 0), Point2D(0, 0.5))),
  PGRAM(listOf(Point2D(0, 1), Point2D(0.5, 1), Point2D(1, 0), Point2D(0.5, 0))),
  ;

  fun centeredPolygon(width: Number, height: Number): Polygon {
    val widthd = width.toDouble()
    val heightd = height.toDouble()
    val realPoints = points.map { Point2D((it.x - 0.5) * widthd, (it.y - 0.5) * heightd) }
    val polygon = pointsToPolygon(realPoints)
    require(polygon.boundsInLocal == BoundingBox(-widthd / 2, -heightd / 2, widthd, heightd))
    return polygon
  }
}
