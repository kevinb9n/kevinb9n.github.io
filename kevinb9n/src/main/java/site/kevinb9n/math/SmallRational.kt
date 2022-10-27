package site.kevinb9n.math

import kotlin.math.absoluteValue
import kotlin.math.sign

data class SmallRational(private val numer: Long, private val denom: Long) {
  init {
    // make sure in canonical form, so eq/hc/ts work right
    require(denom > 0L)
    require(gcd(numer, denom) == 1L)
    if (numer == 0L) require(denom == 1L)
  }

  fun reciprocal() = SmallRational(denom * numer.sign, numer.absoluteValue)

  operator fun times(that: SmallRational): SmallRational {
    val cancel1 = gcd(this.numer, that.denom)
    val cancel2 = gcd(this.denom, that.numer)
    return SmallRational(
      this.numer / cancel1 * (that.numer / cancel2),
      this.denom / cancel2 * (that.denom / cancel1))
  }

  operator fun times(value: Long) = this * of(value)
  // val cancel = gcd(denom, value)
  // return SmallRational(numer * (value / cancel), denom / cancel)

  operator fun div(that: SmallRational) = times(that.reciprocal())
  operator fun div(value: Long) = this / of(value)

  operator fun unaryMinus() = SmallRational(-numer, denom)

  operator fun plus(that: SmallRational): SmallRational {
    val common = lcm(this.denom, that.denom)
    return of(this._times(common) + that._times(common), common)
  }
  private fun _times(multipleOfDenom: Long) = numer * (multipleOfDenom / denom)

  operator fun plus(value: Long) = this + of(value)

  operator fun minus(that: SmallRational) = this + -that
  operator fun minus(value: Long) = this + -value

  fun toDouble(): Double {
    val result = numer.toDouble() / denom.toDouble()
    require(result.isFinite() && !result.isNaN()) { "fuck $this" }
    return result
  }

  override fun toString(): String {
    return "$numer/$denom"
  }

  companion object {
    fun of(numer: Int, denom: Int) = of(numer.toLong(), denom.toLong())

    fun of(numer: Long, denom: Long): SmallRational {
      val divisor = gcd(numer, denom) * denom.sign
      return SmallRational(numer / divisor, denom / divisor)
    }

    fun of(value: Long) = SmallRational(value, 1)
  }
}
