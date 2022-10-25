package site.kevinb9n.math

import site.kevinb9n.plane.Angle.Companion.radians
import site.kevinb9n.plane.Vector2D
import site.kevinb9n.plane.Vector2D.Companion.vector
import kotlin.math.*

interface Field<F : Field<F>> {
  operator fun plus(that: F): F
  operator fun minus(that: F): F
  operator fun times(that: F): F
  operator fun div(that: F): F
}

data class Complex private constructor(private val asVector: Vector2D) : Field<Complex> {
  constructor(re: Double, im: Double) : this(vector(x=re, y=im))

  /** Representing this complex number as `a+bi`, returns `a`. */
  val re = asVector.x

  /** Representing this complex number as `a+bi`, returns `b`. */
  val im = asVector.y

  /** Representing this complex number as `re^(ix)`, returns `r`. */
  fun abs() = asVector.magnitude

  /** Representing this complex number as `re^(ix)`, returns `x`. */
  fun phase() = asVector.direction.radians

  /**
   * There are two numbers that satisfy `i^2 = -1`, and we arbitrarily call one of them `i` and
   * the other one `-i`. What if we'd picked the other way around? A number's "conjugate" is formed
   * by replacing `i` with `-i` and vice versa; that is, `a-bi`, or in polar form `re^-ix`. The
   * sum or product of any number with its conjugate is always a real number.
   */
  fun conjugate() = copy(asVector.reflect())

  operator fun plus(x: Number) = this + fromRe(x)
  override fun plus(that: Complex) = copy(this.asVector + that.asVector)

  operator fun minus(x: Number) = this - fromRe(x)
  override fun minus(that: Complex) = this + -that

  operator fun unaryMinus() = copy(-asVector)

  operator fun times(x: Number) = copy(asVector * x.toDouble())
  override fun times(that: Complex): Complex {
    val conv = conjugate().asVector
    return Complex(conv dot that.asVector, conv cross that.asVector)
  }

  operator fun div(x: Number) = Complex(asVector / x.toDouble())
  override fun div(that: Complex) =
    this * fromPolar(1 / that.abs(), -that.phase())
    // Complex(that.asVector dot this.asVector, that.asVector cross this.asVector) /
    //   that.asVector.magnitudeSquared

  fun distance(that: Complex): Double = (this - that).abs()
  fun nearlyEquals(that: Complex, tolerance: Double) = distance(that) <= tolerance

  fun ln() = Complex(ln(abs()), im)
  fun pow(exponent: Double) = fromPolar(abs().pow(exponent), phase() * exponent)

  fun sin() = Complex(sin(re) * cosh(im), cos(re) * sinh(im))
  fun cos() = Complex(cos(re) * cosh(im), -sin(re) * sinh(im))
  fun tan() = Complex(tan(re), tanh(im)) / Complex(1.0, -tan(re) * tanh(im))

  override fun toString() = "$re + ${im}i"

  companion object {
    val ZERO = fromRe(0.0)
    val I = fromIm(1.0)

    fun fromRe(re: Number) = Complex(re.toDouble(), 0.0)
    fun fromIm(im: Number) = Complex(0.0, im.toDouble())

    fun fromPolar(abs: Number, phase: Number) =
      Complex(vector(magnitude=abs, direction=radians(phase)))
  }
}

operator fun Number.plus(c: Complex) = c + this
operator fun Number.minus(c: Complex) = c - this
operator fun Number.times(c: Complex) = c * this
operator fun Number.div(c: Complex) = c / this
