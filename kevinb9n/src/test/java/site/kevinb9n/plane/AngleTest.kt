package site.kevinb9n.plane

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.plane.Angle.Companion.acos
import site.kevinb9n.plane.Angle.Companion.asin

class AngleTest {
  @Test
  fun testSinAsin() {
    for (seconds in -90*60*60 until 90*60*60) {
      val angle = Angle.seconds(seconds)
      assertThat(asin(angle.sin())).isEqualTo(angle)
    }
  }

  @Test
  fun testCosAcos() {
    for (seconds in 0 until 180*60*60) {
      val angle = Angle.seconds(seconds)
      assertThat(acos(angle.cos())).isEqualTo(angle)
    }
  }

  @Test
  fun testAsinSin() {
    for (input in -1.0 .. 1.0 step .0000008675309) {
      val asin = asin(input)
      assertThat(asin.sin()).isWithin(1e-10).of(input)
    }
  }

  @Test
  fun testAcosCos() {
    for (input in -1.0 .. 1.0 step .0000008675309) {
      val acos = acos(input)
      assertThat(acos.cos()).isWithin(1e-10).of(input)
    }
  }

  infix fun ClosedFloatingPointRange<Double>.step(step: Double) =
    generateSequence(start) { it + step }.takeWhile { it in this }
}
