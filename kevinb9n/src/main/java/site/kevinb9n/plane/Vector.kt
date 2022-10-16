package site.kevinb9n.plane

import kotlin.math.sqrt

interface Vector {
  companion object {
    private val TOLER = 1e-14
    fun vector(
        x: Number? = null,
        y: Number? = null,
        magnitude: Number? = null,
        direction: Angle? = null): Vector {
      val xd = x?.toDouble()
      val yd = y?.toDouble()
      val md = magnitude?.toDouble()

      require(md == null || md >= 0.0)

      val v = if (md == 0.0) {
        PolarVector(0.0, Angle.ZERO)
      } else if (xd != null && yd != null) {
        CartesianVector(xd, yd)
      } else if (md != null && direction != null) {
        PolarVector(md.toDouble(), direction)
      } else if (md != null) {
        if (xd != null) {
          CartesianVector(xd, sqrt(md * md - xd * xd))
        } else {
          CartesianVector(sqrt(md * md - yd!! * yd), yd)
        }
      } else {
        require(direction != null)
        if (xd != null) {
          CartesianVector(xd, direction.tan() * xd) // PolarVector(xd / direction!!.cos(), direction)
        } else {
          CartesianVector(yd!! / direction.tan(), yd) // PolarVector(yd / direction!!.sin(), direction)
        }
      }

      if (xd != null) {
        require(closeEnough(xd, v.x)) { "$xd != ${v.x}" }
      }
      if (yd != null) {
        require(closeEnough(yd, v.y)) { "$yd != ${v.y}" }
      }
      if (md != null) {
        require(closeEnough(magnitude, v.magnitude)) { "$md != ${v.magnitude}" }
      }
      if (direction != null) {
        require(closeEnough(direction.degrees, v.direction.degrees)) {
          "${direction.degrees} != {$v.direction.degrees}"
        }
      }
      return v
    }
    val ZERO = CartesianVector(0, 0)
    operator fun Number.times(v: Vector) = v * this

    fun mean(vararg vectors: Vector) = vectors.reduce(Vector::plus) / vectors.size
  }

  val x: Double
  val y: Double

  val magnitude: Double
  val magsq: Double
  val direction: Angle
  val slope: Double

  fun unitVector(): Vector

  operator fun unaryMinus(): Vector = this * -1.0

  operator fun plus(other: Vector): Vector = CartesianVector(x + other.x, y + other.y)

  operator fun plus(p: Point) = Point(x + p.x, y + p.y)
  operator fun plus(a: Angle) = PolarVector(magnitude, direction + a)

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
}
