package site.kevinb9n.plane

import site.kevinb9n.plane.Angle.Companion.HALF_TURN
import site.kevinb9n.plane.Angle.Companion.cos
import site.kevinb9n.plane.Angle.Companion.sin
import site.kevinb9n.plane.Angle.Companion.tan

data class PolarVector(override val magnitude: Double, override val direction: Angle) : Vector {
  override val x = magnitude * cos(direction)
  override val y = magnitude * sin(direction)
  override val magsq = magnitude * magnitude
  override val slope = tan(direction)

  override fun unitVector() = PolarVector(1.0, direction)

  override fun unaryMinus() = PolarVector(magnitude, direction + HALF_TURN)

  override fun times(scalar: Number) = PolarVector(magnitude * scalar.toDouble(), direction)

  override fun div(scalar: Number) = PolarVector(magnitude / scalar.toDouble(), direction)

  override fun dot(other: Vector) = magnitude * other.magnitude * cos(angleWith(other))

  override fun crossMag(other: Vector) = magnitude * other.magnitude * sin(angleWith(other))

  override fun isLeftTurn(other: Vector) = angleWith(other).normalize().degrees >= 0.0

  override fun collinear(other: Vector) = direction == other.direction

  override fun isHorizontal() = direction == Angle.ZERO || direction == Angle.HALF_TURN

  override fun equals(other: Any?) =
    other is Vector && magnitude == other.magnitude && direction == other.direction
}
