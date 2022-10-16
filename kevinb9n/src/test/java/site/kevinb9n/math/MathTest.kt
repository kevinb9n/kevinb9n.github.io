package site.kevinb9n.math

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class MathTest {
  @Test
  fun what() {
    (-6..6).forEach { a->
      print("${if (a >= 0) " $a" else "$a" }:  ")
      ((-6..-1) + (1..6)).forEach { b->
        val c = a.mod(b)
        print("${if (c >= 0) " $c" else "$c"} ")
      }
      println()
    }
  }

  @Test
  fun floorDivMod() {
    (-10..10).forEach { a ->
      ((-10..-1) + (1..10)).forEach { b ->
        assertThat(a.floorDiv(b) * b + a.mod(b)).isEqualTo(a)
      }
    }
  }

  @Test
  fun divRem() {
    (-10..10).forEach { a ->
      ((-10..-1) + (1..10)).forEach { b ->
        assertThat(a / b * b + a % b).isEqualTo(a)
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
