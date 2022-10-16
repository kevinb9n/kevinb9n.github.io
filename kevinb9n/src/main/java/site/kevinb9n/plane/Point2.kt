package site.kevinb9n.plane

import site.kevinb9n.math.mean
import site.kevinb9n.plane.Vector2.Companion.vector

data class Point2(val x: Double, val y: Double) {

  constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

  operator fun plus(v: Vector2) = Point2(x + v.x, y + v.y)
  operator fun minus(p: Point2) = vector(x - p.x, y - p.y)
  operator fun minus(v: Vector2): Point2 = this + -v

  fun distance(other: Point2) = (this - other).magnitude
  fun midpoint(pt2: Point2) = Point2(mean(x, pt2.x), mean(y, pt2.y)) // this + (pt2 - this) / 2.0

  companion object {
    val ORIGIN = Point2(0, 0)
  }
}
