package site.kevinb9n.plane

import site.kevinb9n.math.closeEnough
import kotlin.math.sqrt

interface Vector2 {
  companion object {
    private val TOLER = 1e-14
    fun vector(
        x: Number? = null,
        y: Number? = null,
        magnitude: Number? = null,
        direction: Angle? = null): Vector2 {
      val xd = x?.toDouble()
      val yd = y?.toDouble()
      val md = magnitude?.toDouble()

      require(md == null || md >= 0.0)

      val v = if (md == 0.0) {
        PolarVector2(0.0, Angle.ZERO)
      } else if (xd != null && yd != null) {
        CartesianVector2(xd, yd)
      } else if (md != null && direction != null) {
        PolarVector2(md.toDouble(), direction)
      } else if (md != null) {
        if (xd != null) {
          CartesianVector2(xd, sqrt(md * md - xd * xd))
        } else {
          CartesianVector2(sqrt(md * md - yd!! * yd), yd)
        }
      } else {
        require(direction != null)
        if (xd != null) {
          CartesianVector2(xd, direction.tan() * xd) // PolarVector2(xd / direction!!.cos(), direction)
        } else {
          CartesianVector2(yd!! / direction.tan(), yd) // PolarVector2(yd / direction!!.sin(), direction)
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
    val ZERO = CartesianVector2(0, 0)
    operator fun Number.times(v: Vector2) = v * this

    fun mean(vararg vectors: Vector2) = vectors.reduce(Vector2::plus) / vectors.size
  }

  val x: Double
  val y: Double

  val magnitude: Double
  val magsq: Double
  val direction: Angle
  val slope: Double

  fun unitVector(): Vector2

  operator fun unaryMinus(): Vector2 = this * -1.0

  operator fun plus(other: Vector2): Vector2 = CartesianVector2(x + other.x, y + other.y)

  operator fun plus(p: Point2) = Point2(x + p.x, y + p.y)
  operator fun plus(a: Angle) = PolarVector2(magnitude, direction + a)

  operator fun minus(other: Vector2): Vector2 = this + -other

  operator fun times(scalar: Number): Vector2

  operator fun div(scalar: Number): Vector2

  /** dot product */
  fun dot(other: Vector2): Double
  fun crossMag(other: Vector2): Double
  fun isLeftTurn(other: Vector2): Boolean
  fun collinear(other: Vector2): Boolean
  fun isHorizontal(): Boolean
  fun angleWith(other: Vector2) = other.direction - direction
}
