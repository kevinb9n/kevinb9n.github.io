package site.kevinb9n.plane

import site.kevinb9n.plane.Angle.Companion.cos
import site.kevinb9n.plane.Angle.Companion.fromRadians
import site.kevinb9n.plane.Angle.Companion.sin
import kotlin.math.acos
import kotlin.math.hypot

data class Vector(val x: Double, val y: Double) {
  constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

  fun magnitude() = hypot(x, y)

  fun direction(): Angle {
    val unitVector = unitVector()
    val sign = if (unitVector.y > 0.0) 1 else -1
    return fromRadians(sign * acos(unitVector.x)).also {
      assert(closeEnough(cos(it), unitVector.x)) { "${cos(it)} != ${unitVector.x}" }
      assert(closeEnough(sin(it), unitVector.y)) { "${sin(it)} != ${unitVector.y}" }
    }
  }

  fun unitVector() = (this / magnitude()).also { it.checkMagnitudeIs(1.0) }

  fun checkMagnitudeIs(mag: Double) = assert(closeEnough(magnitude(), mag)) { magnitude() }

  fun rotate(a: Angle) = from(magnitude(), direction() + a)

  operator fun unaryMinus() = this * -1

  operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
  operator fun plus(p: Point) = p + this

  operator fun minus(other: Vector) = this + -other

  operator fun times(scalar: Number) = Vector(x * scalar.toDouble(), y * scalar.toDouble())

  /** dot product */
  fun dot(other: Vector) = x * other.x + y * other.y
  fun crossMag(other: Vector) = x * other.y - y * other.x

  operator fun div(scalar: Number) = Vector(x / scalar.toDouble(), y / scalar.toDouble())

  companion object {
    val ZERO = Vector(0, 0)
    operator fun Number.times(v: Vector) = v * this
    fun from(magnitude: Number, angle: Angle) = magnitude * angle.unitVector()
  }
}
