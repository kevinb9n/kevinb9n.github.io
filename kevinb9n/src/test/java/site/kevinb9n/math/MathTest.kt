package site.kevinb9n.math

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class MathTest {
  @Test
  fun floorDivMod() { // TODO
    (-10..10).forEach { a ->
      ((-10..-1) + (1..10)).forEach { b ->
        assertThat(a.floorDiv(b) * b + a.mod(b)).isEqualTo(a)
      }
    }
  }

  @Test
  fun divRem() {
    (-10..10).forEach { d ->
      (-10..10).forEach { m ->
        if (m != 0) assertThat(d / m * m + d % m).isEqualTo(d)
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
