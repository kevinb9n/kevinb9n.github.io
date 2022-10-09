package site.kevinb9n.plane

interface Vector {
  val x: Double
  val y: Double

  val magnitude: Double
  val direction: Angle

  fun unitVector(): Vector
  fun rotate(a: Angle) = PolarVector(magnitude, direction + a)

  operator fun unaryMinus(): Vector = this * -1.0

  operator fun plus(other: Vector): Vector = CartesianVector(x + other.x, y + other.y)

  operator fun plus(p: Point) = Point(x + p.x, y + p.y)

  operator fun minus(other: Vector): Vector = this + -other

  operator fun times(scalar: Number): Vector

  operator fun div(scalar: Number): Vector

  /** dot product */
  fun dot(other: Vector): Double
  fun crossMag(other: Vector): Double
  fun isLeftTurn(other: Vector): Boolean
  fun collinear(other: Vector): Boolean
  fun isHorizontal(): Boolean
  fun angleWith(other: Vector) = other.direction - direction

  companion object {
    val ZERO = CartesianVector(0, 0)
    operator fun Number.times(v: Vector) = v * this
  }
}
