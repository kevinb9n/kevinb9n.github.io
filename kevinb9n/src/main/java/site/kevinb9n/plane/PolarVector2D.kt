package site.kevinb9n.plane

import site.kevinb9n.math.closeEnough

data class PolarVector2D(override val magnitude: Double, override val direction: Angle) : Vector2D {
  override val x = magnitude * direction.cos()
  override val y = magnitude * direction.sin()
  override val magsq = magnitude * magnitude
  override val slope = direction.tan()

  override fun unaryMinus() = PolarVector2D(magnitude, direction + Angle.HALF_TURN)

  override fun times(scalar: Number) = PolarVector2D(magnitude * scalar.toDouble(), direction)

  override fun div(scalar: Number) = PolarVector2D(magnitude / scalar.toDouble(), direction)

  override fun dot(other: Vector2D) = magnitude * other.magnitude * angleWith(other).cos()

  override fun cross(other: Vector2D) = magnitude * other.magnitude * angleWith(other).sin()

  override fun isLeftTurn(other: Vector2D) = angleWith(other).degrees >= 0.0

  override fun collinear(other: Vector2D) = direction == other.direction

  override fun isHorizontal() = direction == Angle.ZERO || direction == Angle.HALF_TURN

  override fun equals(other: Any?): Boolean {
    return other is Vector2D && closeEnough(magnitude, other.magnitude)
      && closeEnough(direction.degrees, other.direction.degrees)
  }

  override fun toString(): String {
    return "PolarVector2D(x=$x, y=$y, magnitude=$magnitude, direction=$direction)"
  }
}
