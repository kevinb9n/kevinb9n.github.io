package site.kevinb9n.javafx

import com.google.common.base.Stopwatch
import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.min
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableNumberValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import site.kevinb9n.math.mean
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.roundToInt

fun pointsToPolygon(vararg points: site.kevinb9n.plane.Point2D): Polygon {
  val p = Polygon()
  p.points += points.flatMap { listOf(it.x, it.y) }
  return p
}

fun pointsToPolygon(points: List<site.kevinb9n.plane.Point2D>): Polygon {
  val p = Polygon()
  p.points += points.flatMap { listOf(it.x, it.y) }
  return p
}

fun GraphicsContext.drawPolygon(vertices: List<site.kevinb9n.plane.Point2D>) {
  fillPolygon(
    vertices.map { it.x }.toDoubleArray(),
    vertices.map { it.y }.toDoubleArray(),
    vertices.size)
}

fun box(minCorner: site.kevinb9n.plane.Point2D, width: Number, height: Number) =
  BoundingBox(minCorner.x, minCorner.y, width.toDouble(), height.toDouble())

fun random(maxAbs: Number): Double {
  val r = 2 * Math.random() - 1
  return maxAbs.toDouble() * r
}

fun snapRandom(maxAbs: Number): Double {
  val r = 2 * Math.random() - 1 // works for maxAbs of 1
  return maxAbs.toDouble() * when {
    r > 0.9 -> 1.0
    abs(r) < 0.1 -> 0.0
    r < -0.9 -> -1.0
    else -> r
  }
}

fun printBounds(message: String, node: Node) {
  printBounds("$message local ", node.boundsInLocal)
  printBounds("$message parent", node.boundsInParent)
}
fun printBounds(message: String, bounds: Bounds) {
  val horizBounds = boundsToString(bounds.minX, bounds.maxX, bounds.centerX, bounds.width)
  val vertBounds = boundsToString(bounds.minY, bounds.maxY, bounds.centerY, bounds.height)
  println("$message $horizBounds x $vertBounds")
}

fun boundsToString(min: Double, max: Double, center: Double, extent: Double) : String {
  val minR = round3(min)
  val maxR = round3(max)
  val centerR = round3(center)
  val extentR = round3(extent)
  return "[$minR, $maxR] = $extentR (c $centerR)"
}

fun round3(d: Double) = Math.round(d * 1000) / 1000.0

fun renderToPngFile(node: Node, filename: String) {
  val snap = node.snapshot(SnapshotParameters(), null)
  val fromFXImage = SwingFXUtils.fromFXImage(snap, null)
  ImageIO.write(fromFXImage, "png", File(filename))
}

fun moveToBack(list: ObservableList<Node>, node: Node) {
  list.remove(node)
  list.add(0, node)
  // (list as java.util.List<E>).sort(Comparator.comparing { it != node })
}

// It will modify `node`'s own translateX/Y
// TODO: I don't know why I can't add the event filters to the node itself, i.e. why the
// Group is necessary, but it acts super janky otherwise
class DragToTranslate(val node: Node) : Group(node) {
  private var drag = TranslatingDrag(0.0, 0.0)
  init {
    addEventFilter(MouseEvent.ANY) { it.consume() }
    addEventFilter(MouseEvent.MOUSE_ENTERED) { scene.cursor = Cursor.HAND }
    addEventFilter(MouseEvent.MOUSE_EXITED) { scene.cursor = Cursor.DEFAULT }
    addEventFilter(MouseEvent.MOUSE_PRESSED) {
      drag = TranslatingDrag(it, node) }
    addEventFilter(MouseEvent.MOUSE_DRAGGED) {
      drag.adjust(node, it) }
  }

  private data class TranslatingDrag(val startX: Double, val startY: Double) {
    constructor(pressEvent: MouseEvent, node: Node) : this(
      node.translateX - pressEvent.x,
      node.translateY - pressEvent.y)
    fun adjust(node: Node, event: MouseEvent) {
      node.translateX = event.x + startX
      node.translateY = event.y + startY
    }
  }
}

fun centerBounds(points: List<site.kevinb9n.plane.Point2D>): List<site.kevinb9n.plane.Point2D> {
  if (points.isEmpty()) return points
  val xs = points.map { it.x }
  val ys = points.map { it.y }
  val centerx = mean(xs.minOrNull()!!, xs.maxOrNull()!!)
  val centery = mean(ys.minOrNull()!!, ys.maxOrNull()!!)
  return points.map { site.kevinb9n.plane.Point2D(it.x - centerx, it.y - centery) }
}

fun <T> ObjectProperty<T>.bindObject(vararg dependencies: Observable, supplier: () -> T) =
  bind(Bindings.createObjectBinding(supplier, *dependencies))

fun DoubleProperty.bindDouble(vararg dependencies: Observable, supplier: () -> Double) =
  bind(Bindings.createDoubleBinding(supplier, *dependencies))

fun shapeVisibleProperty(source: Property<Number>, shapeNum: Number): BooleanBinding {
  return object : BooleanBinding() {
    init { bind(source) }
    override fun computeValue() :Boolean {
      return (source.value.toInt() - 1) * shapeNum.toInt() % MAX_SHAPE_INDEX == 0
    }
    override fun getDependencies() = FXCollections.singletonObservableList(source)
    override fun dispose() = unbind(source)
  }
}

fun r(d: Double) = java.lang.String.format("%.4g", d)
fun pt(pt: Point2D) = java.lang.String.format("(%s, %s)", r(pt.x), r(pt.y))
fun pt(pt: Point3D) = java.lang.String.format("(%s, %s)", r(pt.x), r(pt.y))

fun dump(node: Node) {
  println()
  with(node) {
    println("node $id")
    println("type ${javaClass.simpleName}")

    println()
    println("${properties}")
    println()

    println("rotation ${r(rotate)} axis ${pt(rotationAxis)}")
    println("scale ${r(scaleX)} x ${r(scaleY)}")
    println("translate ${r(translateX)} x ${r(translateY)}")
    println("layout ${r(layoutX)} x ${r(layoutY)}")

    printBounds("local", boundsInLocal)
    printBounds("parent", boundsInParent)
    printBounds("layout", layoutBounds)
    if (clip != null) {
      printBounds("clip", clip.boundsInLocal)
    }

    println("computeAreaInScreen: ${r(computeAreaInScreen())}")
    println("localToParent: ${pt(localToParent(0.0, 0.0))} ${pt(localToParent(1.0, 1.0))}")
    println("localToScene: ${pt(localToScene(0.0, 0.0))} ${pt(localToScene(1.0, 1.0))}")
    println("localToScreen: ${pt(localToScreen(0.0, 0.0))} ${pt(localToScreen(1.0, 1.0))}")
    println("isManaged: ${isManaged}, isVisible: ${isVisible}, viewOrder: ${r(viewOrder)}")
  }
  if (node is Region) _dump(node)
  if (node is Shape) _dump(node)
  println()
}

private fun _dump(region: Region) {
  with(region) {
    println("dims ${r(width)} x ${r(height)}")
    println("min dims ${r(minWidth)} x ${r(minHeight)}")
    println("max dims ${r(maxWidth)} x ${r(maxHeight)}")
    println("pref dims ${r(prefWidth)} x ${r(prefHeight)}")
    println("shape = $shape")
    println("scaleShape = ${scaleShapeProperty().get()}, centerShape = ${centerShapeProperty().get()}")
  }
}

private fun _dump(shape: Shape) {
  with(shape) {
    println("fill $fill")
    println("smooth ${smoothProperty().get()}")
    println("stroke $stroke type $strokeType width ${r(strokeWidth)}")
  }
  if (shape is Polygon) _dump(shape)
}

private fun _dump(polygon: Polygon) {
  println("points ${polygon.points}")
}

fun Color.opacityFactor(factor: Double) = this.deriveColor(0.0, 1.0, 1.0, factor)

fun createScalePane(region: Region): StackPane {
  require(region.parent == null)
  require(region.prefWidth != Region.USE_COMPUTED_SIZE)
  require(region.prefHeight != Region.USE_COMPUTED_SIZE)

  val scaleBothProperty = SimpleDoubleProperty()
  val group = Group(region).apply {
    scaleXProperty().bind(scaleBothProperty)
    scaleYProperty().bind(scaleBothProperty)
  }

  return StackPane(group).apply {
    prefWidthProperty().bind(region.prefWidthProperty())
    prefHeightProperty().bind(region.prefHeightProperty())
    scaleBothProperty.bind(min(
      widthProperty().divide(region.prefWidthProperty()),
      heightProperty().divide(region.prefHeightProperty())))
  }
}

fun makeItClipNormally(pane: Pane) {
  val outputClip = Rectangle()
  pane.setClip(outputClip)

  pane.layoutBoundsProperty().addListener { _, _, newValue ->
    outputClip.width = newValue.getWidth()
    outputClip.height = newValue.getHeight()
  }
}

fun sliderFromArrayProperty(slider: Slider, label: Label, array: IntArray, defaultValue: Int, format: String):
  DoubleProperty {
  // Has to be a double, letting lookupByIndexBinding round it, otherwise it gets floored and the
  // slider actually acts weird
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
    label.textProperty().bind(Bindings.format(format, this))
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

fun findBestRotation(polygons: List<Polygon>): Double {
  val stopwatch = Stopwatch.createStarted()
  val neverShown = Group()
  neverShown.children += polygons.map(::clone)

  // It would be fun to find a better algorithm for this
  var best = findBestOf(0 until 180 step 30, neverShown) // 6
  best = findBestOf(best - 25..best + 25 step 10, neverShown) // 6
  best = findBestOf(best - 7..best + 8 step 3, neverShown) // 6
  best = findBestOf(best - 2..best + 2, neverShown) // 5
  println(stopwatch)
  println("angle: $best")
  return best.toDouble()
}

fun clone(it: Polygon) = Polygon().apply {
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
