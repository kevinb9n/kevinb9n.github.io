package site.kevinb9n.plane

import site.kevinb9n.plane.Angle.Companion.cos
import site.kevinb9n.plane.Angle.Companion.fromRadians
import site.kevinb9n.plane.Angle.Companion.sin
import kotlin.math.acos
import kotlin.math.hypot

data class CartesianVector(override val x: Double, override val y: Double) : Vector {
  constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

  override val magnitude = hypot(x, y)
  override val magsq = x * x + y * y

  override val direction: Angle get() {
    val unitVector = unitVector() // or use atan to avoid this?
    val sign = if (unitVector.y > 0.0) 1 else -1
    return fromRadians(sign * acos(unitVector.x)).also {
      assert(closeEnough(cos(it), unitVector.x)) { "${cos(it)} != ${unitVector.x}" }
      assert(closeEnough(sin(it), unitVector.y)) { "${sin(it)} != ${unitVector.y}" }
    }
  }
  override val slope = y / x

  override fun unitVector() = (this / magnitude).also { it.checkMagnitudeIs(1.0) }

  fun checkMagnitudeIs(mag: Double) = assert(closeEnough(magnitude, mag)) { magnitude }

  override operator fun unaryMinus() = this * -1

  override operator fun plus(other: Vector) = CartesianVector(x + other.x, y + other.y)
  override operator fun plus(p: Point) = p + this

  override operator fun minus(other: Vector) = this + -other

  override operator fun times(scalar: Number) = CartesianVector(x * scalar.toDouble(), y * scalar
    .toDouble())

  /** dot product */
  override fun dot(other: Vector) = x * other.x + y * other.y
  override fun crossMag(other: Vector) = x * other.y - y * other.x
  override fun isLeftTurn(other: Vector) = x * other.y > y * other.x
  override fun collinear(other: Vector) = x * other.y == y * other.x // toler?

  override operator fun div(scalar: Number) = CartesianVector(x / scalar.toDouble(), y / scalar.toDouble())

  override fun isHorizontal() = y == 0.0
  override fun equals(other: Any?) = other is Vector && x == other.x && y == other.y
}
