package site.kevinb9n.plane

data class PolarVector(override val magnitude: Double, override val direction: Angle) : Vector {
  override val x = magnitude * direction.cos()
  override val y = magnitude * direction.sin()
  override val magsq = magnitude * magnitude
  override val slope = direction.tan()

  override fun unitVector() = PolarVector(1.0, direction)

  override fun unaryMinus() = PolarVector(magnitude, direction + Angle.HALF_TURN)

  override fun times(scalar: Number) = PolarVector(magnitude * scalar.toDouble(), direction)

  override fun div(scalar: Number) = PolarVector(magnitude / scalar.toDouble(), direction)

  override fun dot(other: Vector) = magnitude * other.magnitude * angleWith(other).cos()

  override fun crossMag(other: Vector) = magnitude * other.magnitude * angleWith(other).sin()

  override fun isLeftTurn(other: Vector) = angleWith(other).degrees >= 0.0

  override fun collinear(other: Vector) = direction == other.direction

  override fun isHorizontal() = direction == Angle.ZERO || direction == Angle.HALF_TURN

  override fun equals(other: Any?): Boolean {
    return other is Vector && closeEnough(magnitude, other.magnitude) && closeEnough(direction.degrees, other.direction.degrees)
  }
}
