package site.kevinb9n.plane

import site.kevinb9n.math.closeEnough
import site.kevinb9n.plane.Angle.Companion.radians
import kotlin.math.atan2
import kotlin.math.hypot

data class CartesianVector2D internal constructor(override val x: Double, override val y: Double) :
Vector2D {
  internal constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

  override val magnitude = hypot(x, y)
  override val magsq = x * x + y * y

  override val direction = radians(atan2(y, x))
  override val slope = y / x

  fun checkMagnitudeIs(mag: Double) = assert(closeEnough(magnitude, mag)) { magnitude }

  override operator fun unaryMinus() = this * -1

  override operator fun plus(other: Vector2D) = CartesianVector2D(x + other.x, y + other.y)
  override operator fun plus(point: Point2D) = point + this

  override operator fun minus(other: Vector2D) = this + -other

  override operator fun times(scalar: Number) = CartesianVector2D(x * scalar.toDouble(), y * scalar
    .toDouble())

  /** dot product */
  override fun dot(other: Vector2D) = x * other.x + y * other.y
  override fun cross(other: Vector2D) = x * other.y - y * other.x
  override fun isLeftTurn(other: Vector2D) = x * other.y > y * other.x
  override fun collinear(other: Vector2D) = x * other.y == y * other.x // toler?

  override operator fun div(scalar: Number) = CartesianVector2D(x / scalar.toDouble(), y / scalar.toDouble())

  override fun isHorizontal() = y == 0.0

  override fun equals(other: Any?) = other is Vector2D && closeEnough(x, other.x) && closeEnough(y, other.y)

  override fun toString(): String {
    return "CartesianVector2D(x=$x, y=$y, magnitude=$magnitude, direction=$direction)"
  }
}
