package site.kevinb9n.math

import site.kevinb9n.plane.Angle
import site.kevinb9n.plane.Angle.Companion.radians
import site.kevinb9n.plane.Vector2D
import site.kevinb9n.plane.Vector2D.Companion.vector
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.tan
import kotlin.math.tanh

data class Complex(val v: Vector2D) {
  constructor(re: Number, im: Number) : this(vector(re, im))

  /** Representing this number as `(a + bi)`, returns `a`. */
  val re = v.x

  /** Representing this number as `(a + bi)`, returns `b`. */
  val im = v.y

  /** Representing this number as `re^(ix)`, returns `r`. */
  fun abs() = v.magnitude

  /** Representing this number as `re^(ix)`, returns `x`. */
  fun theta() = v.direction

  /** Returns `a-bi`, or `re^(-ix)` */
  fun conjugate() = Complex(v.reflect())

  operator fun plus(x: Number) = this + fromRe(x)
  operator fun plus(that: Complex) = Complex(this.v + that.v)

  operator fun minus(x: Number) = this - fromRe(x)
  operator fun minus(that: Complex) = Complex(this.v - that.v)

  operator fun unaryMinus() = this * -1.0

  operator fun times(x: Number) = Complex(v * x)
  operator fun times(that: Complex): Complex {
    val conv = conjugate().v
    return Complex(conv dot that.v, conv cross that.v)
  }

  operator fun div(x: Number) = Complex(v / x)
  operator fun div(that: Complex): Complex {
    return Complex(that.v dot this.v, that.v cross this.v) / that.v.magsq
  }

  fun sin() = Complex(sin(re) * cosh(im), cos(re) * sinh(im))
  fun cos() = Complex(cos(re) * cosh(im), sin(re) * sinh(im))
  fun tan() = Complex(tan(re), tanh(im)) / Complex(1.0, -tan(re) * tanh(im))

  override fun toString() = "$re + ${im}i"

  companion object {
    val ZERO = fromRe(0.0)
    val I = fromIm(1.0)

    fun fromRe(re: Number) = Complex(re.toDouble(), 0.0)
    fun fromIm(im: Number) = Complex(0.0, im.toDouble())

    fun fromPolar(mag: Number, theta: Number) =
      Complex(vector(magnitude=mag, direction=radians(theta)))
  }
}
