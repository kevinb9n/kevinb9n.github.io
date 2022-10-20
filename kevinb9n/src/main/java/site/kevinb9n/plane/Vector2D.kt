package site.kevinb9n.plane

import site.kevinb9n.math.closeEnough
import kotlin.math.sqrt

interface Vector2D : AffineVector<Point2D, Vector2D> {
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

      require(md == null || md >= 0.0) { "x $x y $y mag $md dir $direction" }

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

    fun mean(vararg vectors: Vector2D) = vectors.reduce(Vector2D::plus) / vectors.size.toDouble()
  }

  override val magnitude: Double

  val x: Double
  val y: Double
  val magnitudeSquared: Double
  val direction: Angle
  val slope: Double

  override fun plus(that: Vector2D) = CartesianVector2D(x + that.x, y + that.y)
  override fun plus(point: Point2D) = Point2D(x + point.x, y + point.y)

  fun reflect(): Vector2D
  fun rotate(angle: Angle) = PolarVector(magnitude, direction + angle)
  infix fun dot(that: Vector2D): Double
  infix fun cross(that: Vector2D): Double
  fun isLeftTurn(that: Vector2D): Boolean
  fun collinear(that: Vector2D): Boolean
  fun isHorizontal(): Boolean
  fun angleWith(that: Vector2D) = that.direction - this.direction
}
