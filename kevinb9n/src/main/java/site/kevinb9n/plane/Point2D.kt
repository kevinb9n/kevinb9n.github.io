package site.kevinb9n.plane

import site.kevinb9n.plane.Vector2D.Companion.vector

/** A two-dimensional point on the coordinate plane, with `Double` coordinates. */
data class Point2D(val x: Double, val y: Double) : AffinePoint<Point2D, Vector2D> {
  constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

  override fun plus(that: Vector2D) = Point2D(this.x + that.x, this.y + that.y)
  override fun minus(that: Point2D) = vector(this.x - that.x, this.y - that.y)

  fun distance(that: Point2D) = (this - that).magnitude
  companion object {
    val ORIGIN = Point2D(0, 0)
  }
}
