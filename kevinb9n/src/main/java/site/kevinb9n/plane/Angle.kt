package site.kevinb9n.plane

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.tan

data class Angle(private val seconds: Double) {
  val degrees = seconds / 60.0 / 60.0
  val turns = degrees / 360.0
  val radians = turns * 2.0 * PI

  fun unitVector() = Vector(cos(this), sin(this))

  /** Returns this angle expressed in the range [-HALF_TURN, HALF_TURN) */
  fun normalize() = seconds.modWithMininum(TURN.seconds, -HALF_TURN.seconds)

  /** Returns this angle expressed in the range [0, TURN) */
  fun normalizeUnsigned() = fromSeconds(seconds % TURN.seconds)

  operator fun unaryMinus() = fromSeconds(-seconds)
  operator fun plus(other: Angle) = fromSeconds(seconds + other.seconds)
  operator fun minus(other: Angle) = this + -other
  operator fun times(scalar: Number) = fromSeconds(seconds * scalar.toDouble())
  operator fun div(scalar: Number) = fromSeconds(seconds / scalar.toDouble())

  companion object {
    val ZERO = fromSeconds(0.0)

    fun fromSeconds(s: Number) = Angle(fixNearInteger(s.toDouble(), 1e-9))
    fun fromMinutes(m: Number) = fromSeconds(m.toDouble() * 60.0)
    fun fromDegrees(d: Number) = fromMinutes(d.toDouble() * 60.0)
    fun fromTurns  (t: Number) = fromDegrees(t.toDouble() * 360.0)

    fun fromRadians(r: Number): Angle {
      val candidate = fromTurns(r.toDouble() / 2.0 / PI)
      val seconds = round(candidate.seconds)
      return if (abs(seconds - candidate.seconds) > 1e-9) candidate else fromSeconds(seconds)
    }

    val TURN = fromTurns(1.0)
    val HALF_TURN = fromTurns(0.5)

    fun sin(a: Angle) = sin(a.radians)
    fun cos(a: Angle) = cos(a.radians)
    fun tan(a: Angle) = tan(a.radians)

  }
}
