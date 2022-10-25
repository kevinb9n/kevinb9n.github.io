package site.kevinb9n.math

import kotlin.math.absoluteValue
import kotlin.math.sign

class SmallRational(private val numer: Long, private val denom: Long) {
  init {
    require(denom > 0L)
    require(gcd(numer, denom) == 1L)
    if (numer == 0L) require(denom == 1L)
  }

  fun reciprocal(): SmallRational {
    require(numer != 0L)
    return SmallRational(denom * numer.sign, numer.absoluteValue)
  }

  operator fun times(that: SmallRational): SmallRational {
    if (this == ZERO || that == ZERO) return ZERO
    val div1 = gcd(this.numer, that.denom)
    val div2 = gcd(this.denom, that.numer)
    return SmallRational(this.numer / div1 * (that.numer / div2), this.denom / div2 * (that.denom / div1))
  }
  operator fun times(that: Long) = this * SmallRational(that, 1)

  operator fun div(that: SmallRational) = times(that.reciprocal())
  operator fun div(that: Long) = this * SmallRational(1, that)

  operator fun unaryMinus() = SmallRational(-numer, denom)

  operator fun plus(that: SmallRational): SmallRational {
    val common = lcm(this.denom, that.denom)
    return of(
      common / this.denom * this.numer +
      common / that.denom * that.numer, common)
  }
  operator fun plus(that: Long) = this + SmallRational(that, 1)

  operator fun minus(that: SmallRational) = this + -that
  operator fun minus(that: Long) = this + -that

  fun toDouble(): Double {
    val result = numer.toDouble() / denom.toDouble()
    require(result.isFinite() && !result.isNaN()) { "fuck $this" }
    return result
  }

  override fun toString(): String {
    return "$numer/$denom"
  }

  companion object {
    val ZERO = SmallRational(0, 1)

    fun of(numer: Long, denom: Long): SmallRational {
      require(denom != 0L)
      if (numer == 0L) return ZERO
      val divisor = gcd(numer, denom) * denom.sign
      return SmallRational(numer / divisor, denom / divisor)
    }
  }
}
