package site.kevinb9n.plane

import site.kevinb9n.math.fixNearInteger
import site.kevinb9n.math.modWithMinimum
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * A **normalized** angle in the range [-180, 180) degrees / [-pi, pi) radians.
 * Operations are cyclic. Internal representation is seconds (1/3600 degree) so that many
 * common angles can be represented exactly.
 */
data class Angle private constructor(val seconds: Double) {
  init {
    require(seconds >= -HALF_TURN_IN_SECONDS) { "$seconds out of range"}
    require(seconds < HALF_TURN_IN_SECONDS) { "$seconds out of range"}
  }
  val degrees = seconds / 60.0 / 60.0
  val turns = degrees / 360.0
  val radians = turns * 2.0 * PI

  // Reflection about the x-axis, not rotation of 180 degrees.
  // turns(-0.5) is its own negation, but this actually makes reasonable sense
  operator fun unaryMinus() = seconds(-seconds)

  operator fun plus(that: Angle) = seconds(this.seconds + that.seconds)
  operator fun minus(that: Angle) = this + -that
  operator fun times(scalar: Number) = seconds(seconds * scalar.toDouble())

  // Any other operations?
  // Useless or senseless: unaryPlus contains set invoke inc/dec get
  // Weird: div/rem compareTo rangeTo
  // Automatically handled: *Assign

  fun sin() = sin(radians)
  fun cos() = cos(radians)
  fun tan() = tan(radians)
  fun csc() = 1 / sin()
  fun sec() = 1 / cos()
  fun cot() = 1 / tan()

  fun isHorizontal() = seconds == 0.0 || seconds == HALF_TURN_IN_SECONDS
  fun isVertical() = seconds.absoluteValue == HALF_TURN_IN_SECONDS / 2.0

  companion object {
    private val FULL_TURN_IN_SECONDS = 360.0 * 60.0 * 60.0
    private val HALF_TURN_IN_SECONDS = FULL_TURN_IN_SECONDS / 2.0
    val ZERO = seconds(0.0)
    val HALF_TURN = turns(0.5) // gets normalized to -0.5 but who cares

    fun seconds(s: Number): Angle {
      val seconds = fixNearInteger(s.toDouble(), 3e-6)
      val normalized = seconds.modWithMinimum(FULL_TURN_IN_SECONDS, -HALF_TURN_IN_SECONDS)
      return Angle(normalized)
    }

    fun degrees(d: Number) = seconds(d.toDouble() * 60.0 * 60.0)
    fun turns  (t: Number) = degrees(t.toDouble() * 360.0)
    fun radians(r: Number) = degrees(r.toDouble() / degrees(1.0).radians)

    fun asin(sin: Double) = radians(kotlin.math.asin(sin))
    fun acos(cos: Double) = radians(kotlin.math.acos(cos))
    fun atan(tan: Double) = radians(kotlin.math.atan(tan))

  }
}
