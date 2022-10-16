package site.kevinb9n.plane

import site.kevinb9n.math.closeEnough

data class PolarVector2(override val magnitude: Double, override val direction: Angle) : Vector2 {
  override val x = magnitude * direction.cos()
  override val y = magnitude * direction.sin()
  override val magsq = magnitude * magnitude
  override val slope = direction.tan()

  override fun unitVector() = PolarVector2(1.0, direction)

  override fun unaryMinus() = PolarVector2(magnitude, direction + Angle.HALF_TURN)

  override fun times(scalar: Number) = PolarVector2(magnitude * scalar.toDouble(), direction)

  override fun div(scalar: Number) = PolarVector2(magnitude / scalar.toDouble(), direction)

  override fun dot(other: Vector2) = magnitude * other.magnitude * angleWith(other).cos()

  override fun crossMag(other: Vector2) = magnitude * other.magnitude * angleWith(other).sin()

  override fun isLeftTurn(other: Vector2) = angleWith(other).degrees >= 0.0

  override fun collinear(other: Vector2) = direction == other.direction

  override fun isHorizontal() = direction == Angle.ZERO || direction == Angle.HALF_TURN

  override fun equals(other: Any?): Boolean {
    return other is Vector2 && closeEnough(magnitude, other.magnitude)
      && closeEnough(direction.degrees, other.direction.degrees)
  }

  override fun toString(): String {
    return "PolarVector2(x=$x, y=$y, magnitude=$magnitude, direction=$direction)"
  }
}
