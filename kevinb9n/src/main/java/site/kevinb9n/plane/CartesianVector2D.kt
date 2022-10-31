package site.kevinb9n.plane

import site.kevinb9n.plane.Angle.Companion.radians
import site.kevinb9n.plane.Vector2D.Companion.vector
import kotlin.math.atan2
import kotlin.math.hypot

data class CartesianVector2D internal constructor(
    override val x: Double, override val y: Double) : Vector2D {
  internal constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

  override val magnitude = hypot(x, y)
  override val magnitudeSquared = x * x + y * y

  override val direction = radians(atan2(y, x))
  override val slope = y / x

  override fun reflect() = vector(x, -y)
  override operator fun unaryMinus() = this * -1.0

  override operator fun plus(that: Vector2D) = copy(this.x + that.x, this.y + that.y)
  override operator fun plus(point: Point2D) = point + this

  override operator fun minus(other: Vector2D) = this + -other

  override operator fun times(scalar: Double) = copy(x * scalar, y * scalar)

  override fun dot(that: Vector2D)        = this.x * that.x + this.y * that.y
  override fun crossZ(that: Vector2D)      = this.x * that.y - this.y * that.x
  override fun isLeftTurn(that: Vector2D) = crossZ(that) > 0.0
  override fun collinear(that: Vector2D)  = crossZ(that) == 0.0 // toler??

  override operator fun div(scalar: Double) = copy(x / scalar, y / scalar)

  override fun equals(other: Any?) = equalsImpl(other)
  override fun hashCode() = hashCodeImpl()
}
