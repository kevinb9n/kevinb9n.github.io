package site.kevinb9n.javafx

import javafx.collections.ObservableList
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Polygon
import java.io.File
import java.util.Comparator
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
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

fun polygon(vararg points: Point): Polygon {
  val p = Polygon()
  p.points += points.flatMap { listOf(it.x, it.y) }
  return p
}

fun polygon(points: List<Point>): Polygon {
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

fun scaleToFit(bound: Bounds, desired: Bounds): Double {
  return min(desired.width / bound.width, desired.height / bound.height)
}

fun snapRandom(maxAbs: Number): Double {
  val r = 2 * Math.random() - 1 // works for maxAbs of 1
  return round(maxAbs.toDouble() * when {
    r > 0.8 -> 1.0
    abs(r) < 0.2 -> 0.0
    r < -0.8 -> -1.0
    else -> r
  })
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
