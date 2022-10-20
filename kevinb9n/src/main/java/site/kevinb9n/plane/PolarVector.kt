package site.kevinb9n.plane

data class PolarVector(override val magnitude: Double, override val direction: Angle) : Vector2D {
  init {
    require(magnitude >= 0.0) { magnitude }
  }
  override val x = magnitude * direction.cos()
  override val y = magnitude * direction.sin()
  override val magnitudeSquared = magnitude * magnitude
  override val slope = direction.tan()

  override fun reflect() = copy(direction = -direction)

  override fun unaryMinus() = rotate(Angle.HALF_TURN)

  override fun times(scalar: Double): PolarVector {
    return if (scalar >= 0.0) copy(magnitude * scalar) else -copy(magnitude * -scalar)
  }

  override fun div(scalar: Double) = 1.0 / scalar * this

  override fun dot(that: Vector2D) = this.magnitude * that.magnitude * this.angleWith(that).cos()

  override fun cross(that: Vector2D) = this.magnitude * that.magnitude * this.angleWith(that).sin()

  override fun isLeftTurn(that: Vector2D) = this.angleWith(that).degrees >= 0.0

  override fun collinear(that: Vector2D) = this.direction == that.direction

  override fun isHorizontal() = direction == Angle.ZERO || direction == Angle.HALF_TURN

  override fun equals(that: Any?): Boolean {
    return that is Vector2D && this.magnitude == that.magnitude && this.direction == that.direction
  }

  override fun toString(): String {
    return "PolarVector(x=$x, y=$y, magnitude=$magnitude, direction=$direction)"
  }
}
