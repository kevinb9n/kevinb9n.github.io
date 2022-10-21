package site.kevinb9n.math

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.jupiter.api.Test
import kotlin.math.pow

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
}
