package site.kevinb9n.math

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.jupiter.api.Test

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
  fun testGcd() {
    (0..9).forEach {
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
