package site.kevinb9n.math

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.jupiter.api.Test
import site.kevinb9n.javafx.random
import java.math.BigDecimal
import java.math.RoundingMode
import java.math.RoundingMode.HALF_EVEN

class MathTest {
  val r = java.util.Random()

  @Test
  fun divRemI() {
    val divRem = DivModPair({a,b-> a/b}, {a,b->a%b})
    for (i in 1..1000) {
      divRem.check(r.nextInt(60) - 30, r.nextInt(20) - 10)
    }
  }

  @Test
  fun floorDivModI() {
    val floorMod = DivModPair(Int::floorDiv, Int::mod)
    for (i in 1..1000) {
      floorMod.check(r.nextInt(60) - 30, r.nextInt(20) - 10)
    }
  }

  data class DivModPair(
    val divver: (Int, Int) -> Int,
    val modder: (Int, Int) -> Int) {
    fun check(a: Int, b: Int) {
      if (b != 0) {
        assertWithMessage("$a $b").that(divver(a, b) * b + modder(a, b)).isEqualTo(a)
      }
    }
  }

  @Test
  fun factors() {
    assertThat(factors(0)).isEmpty()
    assertThat(factors(1)).containsExactly(1)
    assertThat(factors(2)).containsExactly(1, 2)
    assertThat(factors(3)).containsExactly(1, 3)
    assertThat(factors(4)).containsExactly(1, 2, 4)
    assertThat(factors(6)).containsExactly(1, 2, 3, 6).inOrder()
    assertThat(factors(8)).containsExactly(1, 2, 4, 8).inOrder()
    assertThat(factors(900)).containsExactly(
      1, 900,
      2, 450,
      3, 300,
      4, 225,
      5, 180,
      6, 150,
      9, 100,
      10, 90,
      12, 75,
      15, 60,
      18, 50,
      20, 45,
      25, 36,
      30)
  }

  @Test
  fun testRoundToBinaryDecimalPlaces() {
    val d = 1.0 / 3.0
    var current = 0.0
    var incr = 0.5
    for (places in 0..52) {
      assertThat(roundToBinaryDecimalPlaces(d, places)).isEqualTo(current)
      current += incr
      incr *= -0.5
    }
    assertThat(roundToBinaryDecimalPlaces(d, 52)).isEqualTo(0.333_333_333_333_333_259)
  }

  @Test
  fun testGcd() {
    (0..12).forEach {
      assertThat(gcd(it, 0)).isEqualTo(it)
      assertThat(gcd(-it, 0)).isEqualTo(it)
      assertThat(gcd(0, it)).isEqualTo(it)
      assertThat(gcd(0, -it)).isEqualTo(it)
      assertThat(gcd(it, it)).isEqualTo(it)
      assertThat(gcd(it, -it)).isEqualTo(it)
      assertThat(gcd(-it, it)).isEqualTo(it)
      assertThat(gcd(-it, -it)).isEqualTo(it)

      assertThat(gcd(it, 1)).isEqualTo(1)
      assertThat(gcd(-it, 1)).isEqualTo(1)

      assertThat(gcd(it, 2 * it)).isEqualTo(it)
      assertThat(gcd(it, -2 * it)).isEqualTo(it)
      assertThat(gcd(-it, 2 * it)).isEqualTo(it)
      assertThat(gcd(-it, -2 * it)).isEqualTo(it)
    }
    assertThat(gcd(4, 6)).isEqualTo(2)
    assertThat(gcd(4, 10)).isEqualTo(2)
    assertThat(gcd(6, 9)).isEqualTo(3)
  }

  // 63 decimal places has the weird property this closest approximation for PI is 144 times
  // the closest approximation for pi/144, so a great many common angles will work out well
  val PI = BigDecimal("3.141592653589793238462643383279502884197169399375105820974944592")

  @Test
  fun testSine() {
    assertThat(sineTo63Digits(BigDecimal.ZERO)).isEqualTo(BigDecimal.ZERO)
    assertThat((sineTo63Digits(PI / 10) + BigDecimal(0.25)).pow(2).stdize())
      .isEqualTo(BigDecimal(0.3125))
    assertThat(sineTo63Digits(PI / 6)).isEqualTo(BigDecimal(0.5))
    assertThat(sineTo63Digits(PI / 4).pow(2).stdize()).isEqualTo(BigDecimal(0.5))
    assertThat(sineTo63Digits(PI / 3).pow(2).stdize()).isEqualTo(BigDecimal(0.75))
    assertThat(sineTo63Digits(PI / 2)).isEqualTo(BigDecimal.ONE)
    assertThat(sineTo63Digits(PI)).isEqualTo(BigDecimal.ZERO)
    assertThat(sineTo63Digits(-PI / 6)).isEqualTo(BigDecimal(-0.5))
  }

  operator fun BigDecimal.plus(a: Int): BigDecimal = this + BigDecimal(a)
  operator fun BigDecimal.minus(a: Int): BigDecimal = this - BigDecimal(a)
  operator fun BigDecimal.times(a: Int): BigDecimal = this * BigDecimal(a)
  operator fun BigDecimal.div(a: Int): BigDecimal = this / BigDecimal(a)

  @Test
  fun testCompare() {
    for (i in 1..100_000) {
      val d = random(kotlin.math.PI)
      assertWithMessage("$d").that(
        sineTo63Digits(BigDecimal(d)).toDouble()).isWithin(1.15e-16).of(kotlin.math.sin(d))
    }
  }

  fun BigDecimal.stdize() = this.setScale(63, HALF_EVEN).stripTrailingZeros()
}
