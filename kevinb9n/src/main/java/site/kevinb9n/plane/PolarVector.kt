package site.kevinb9n.plane

data class PolarVector(override val magnitude: Double, override val direction: Angle) : Vector2D {
  override val x = magnitude * direction.cos()
  override val y = magnitude * direction.sin()
  override val magsq = magnitude * magnitude
  override val slope = direction.tan()

  override fun reflect() = Vector2D.vector(magnitude=magnitude, direction=-direction)

  override fun unaryMinus() = PolarVector(magnitude, direction + Angle.HALF_TURN)

  override fun times(scalar: Number) = PolarVector(magnitude * scalar.toDouble(), direction)

  override fun div(scalar: Number) = PolarVector(magnitude / scalar.toDouble(), direction)

  override fun dot(other: Vector2D) = magnitude * other.magnitude * angleWith(other).cos()

  override fun cross(other: Vector2D) = magnitude * other.magnitude * angleWith(other).sin()

  override fun isLeftTurn(other: Vector2D) = angleWith(other).degrees >= 0.0

  override fun collinear(other: Vector2D) = direction == other.direction

  override fun isHorizontal() = direction == Angle.ZERO || direction == Angle.HALF_TURN

  override fun equals(other: Any?): Boolean {
    return other is Vector2D && magnitude == other.magnitude && direction == other.direction
  }

  override fun toString(): String {
    return "PolarVector(x=$x, y=$y, magnitude=$magnitude, direction=$direction)"
  }
}
