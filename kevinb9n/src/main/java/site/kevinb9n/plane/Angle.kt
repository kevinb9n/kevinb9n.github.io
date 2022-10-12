package site.kevinb9n.plane

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

data class Angle private constructor(private val seconds: Double) {
  init {
    require(seconds >= -HALF_TURN_IN_SECONDS)
    require(seconds < HALF_TURN_IN_SECONDS)
  }
  val degrees = seconds / 60.0 / 60.0
  val turns = degrees / 360.0
  val radians = turns * 2.0 * PI

  operator fun unaryMinus() = seconds(-seconds)
  operator fun plus(other: Angle) = seconds(seconds + other.seconds)
  operator fun minus(other: Angle) = this + -other
  operator fun times(scalar: Number) = seconds(seconds * scalar.toDouble())

  fun sin() = sin(radians)
  fun cos() = cos(radians)
  fun tan() = tan(radians)

  companion object {
    private val FULL_TURN_IN_SECONDS = 360.0 * 60.0 * 60.0
    private val HALF_TURN_IN_SECONDS = FULL_TURN_IN_SECONDS / 2.0
    val ZERO = seconds(0.0)
    val HALF_TURN = turns(0.5) // gets normalized to -0.5 but who cares

    fun seconds(s: Number): Angle {
      val seconds = fixNearInteger(s.toDouble(), 1e-5)
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
