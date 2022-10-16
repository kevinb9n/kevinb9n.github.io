package site.kevinb9n.plane

import site.kevinb9n.math.closeEnough
import kotlin.math.sqrt

interface Vector2D : Vector<Point2D, Vector2D> {
  companion object {
    private val TOLER = 1e-14
    fun vector(
        x: Number? = null,
        y: Number? = null,
        magnitude: Number? = null,
        direction: Angle? = null): Vector2D {
      val xd = x?.toDouble()
      val yd = y?.toDouble()
      val md = magnitude?.toDouble()

      require(md == null || md >= 0.0)

      val v = if (md == 0.0) {
        PolarVector(0.0, Angle.ZERO)
      } else if (xd != null && yd != null) {
        CartesianVector2D(xd, yd)
      } else if (md != null && direction != null) {
        PolarVector(md.toDouble(), direction)
      } else if (md != null) {
        if (xd != null) {
          CartesianVector2D(xd, sqrt(md * md - xd * xd))
        } else {
          CartesianVector2D(sqrt(md * md - yd!! * yd), yd)
        }
      } else {
        require(direction != null)
        if (xd != null) {
          CartesianVector2D(xd, direction.tan() * xd) // PolarVector(xd / direction!!.cos(), direction)
        } else {
          CartesianVector2D(yd!! / direction.tan(), yd) // PolarVector(yd / direction!!.sin(), direction)
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
    val ZERO = CartesianVector2D(0, 0)

    fun mean(vararg vectors: Vector2D) = vectors.reduce(Vector2D::plus) / vectors.size
  }

  override val magnitude: Double

  val x: Double
  val y: Double
  val magsq: Double
  val direction: Angle
  val slope: Double

  override fun plus(other: Vector2D) = CartesianVector2D(x + other.x, y + other.y)
  override fun plus(point: Point2D) = Point2D(x + point.x, y + point.y)

  fun rotate(angle: Angle) = PolarVector(magnitude, direction + angle)
  fun dot(other: Vector2D): Double
  fun cross(other: Vector2D): Double
  fun isLeftTurn(other: Vector2D): Boolean
  fun collinear(other: Vector2D): Boolean
  fun isHorizontal(): Boolean
  fun angleWith(other: Vector2D) = other.direction - direction // TODO: reverse??
}
