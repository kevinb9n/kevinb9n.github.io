package site.kevinb9n.plane

import site.kevinb9n.plane.Vector2D.Companion.vector

/** A two-dimensional point on the coordinate plane, with `Double` coordinates. */
data class Point2D(val x: Double, val y: Double) : Point<Point2D, Vector2D> {
  constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

  override fun plus(v: Vector2D) = Point2D(x + v.x, y + v.y)
  override fun minus(p: Point2D) = vector(x - p.x, y - p.y)
  override fun minus(v: Vector2D): Point2D = this + -v

  override fun midpoint(other: Point2D) = this + (other - this) / 2.0
  override fun reflectAbout(other: Point2D) = other - this + other

  fun distance(other: Point2D) = (this - other).magnitude

  companion object {
    val ORIGIN = Point2D(0, 0)
  }
}
