package site.kevinb9n.javafx

import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableNumberValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

data class Point(val x: Double, val y: Double) {
  constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

  fun translate(distance: Number, degrees: Number): Point {
    val radians = Math.toRadians(degrees.toDouble())
    return Point(
      x + distance.toDouble() * cos(radians),
      y + distance.toDouble() * sin(radians))
  }
}

fun pointsToPolygon(vararg points: Point): Polygon {
  val p = Polygon()
  p.points += points.flatMap { listOf(it.x, it.y) }
  return p
}

fun pointsToPolygon(points: List<Point>): Polygon {
  val p = Polygon()
  p.points += points.flatMap { listOf(it.x, it.y) }
  return p
}

fun GraphicsContext.drawPolygon(vertices: List<Point>) {
  fillPolygon(
    vertices.map { it.x }.toDoubleArray(),
    vertices.map { it.y }.toDoubleArray(),
    vertices.size)
}

fun box(minCorner: Point, maxCorner: Point) = box(
  minCorner,
  maxCorner.x - minCorner.x,
  maxCorner.y - minCorner.y)

fun box(minCorner: Point, width: Number, height: Number) =
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
  val bounds = node.boundsInParent
  printBounds("$message / horiz", bounds.minX, bounds.maxX, bounds.centerX, bounds.width)
  printBounds("$message /  vert", bounds.minY, bounds.maxY, bounds.centerY, bounds.height)
}

fun printBounds(
  message: String, min: Double, max: Double, center: Double, extent: Double) {
  val minR = round(min)
  val maxR = round(max)
  val centerR = round(center)
  val extentR = round(extent)
  println("$message: [$minR, $maxR] = $extentR (c $centerR)")
}

fun round(d: Double) = Math.round(d * 1000) / 1000.0

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
      println("press")
      drag = TranslatingDrag(it, node) }
    addEventFilter(MouseEvent.MOUSE_DRAGGED) {
      println("drag")
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

fun factors(value: Int): IntArray = (1 .. value).filter { value % it == 0 }.toIntArray()
fun mean(a: Double, b: Double) = (a + b) / 2.0
fun centerBounds(points: List<Point>): List<Point> {
  if (points.isEmpty()) return points
  val xs = points.map { it.x }
  val ys = points.map { it.y }
  val centerx = mean(xs.minOrNull()!!, xs.maxOrNull()!!)
  val centery = mean(ys.minOrNull()!!, ys.maxOrNull()!!)
  return points.map { Point(it.x - centerx, it.y - centery) }
}

fun <T> ObjectProperty<T>.bindObject(vararg deps: Observable, supplier: () -> T) =
  bind(Bindings.createObjectBinding(supplier, *deps))

fun shapeVisibleProperty(source: IntegerProperty, shapeNum: Number): BooleanBinding {
  return object : BooleanBinding() {
    init { bind(source) }
    override fun computeValue() :Boolean {
      return (source.value - 1) * shapeNum.toInt() % MAX_SHAPE_INDEX == 0
    }
    override fun getDependencies() = FXCollections.singletonObservableList(source)
    override fun dispose() = unbind(source)
  }
}

fun Color.opacityFactor(factor: Double) = this.deriveColor(0.0, 1.0, 1.0, factor)
