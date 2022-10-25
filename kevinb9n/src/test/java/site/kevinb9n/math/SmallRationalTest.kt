package site.kevinb9n.math

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.math.SmallRational.Companion.ZERO
import kotlin.math.absoluteValue
import kotlin.random.Random.Default.nextLong

class SmallRationalTest {
  @Test
  fun fuck() {
    for (i in 1..1_000_000) {
      val rat1 = SmallRational.of(nextLong(-1000L, 1000L), nextLong(1L, 100L))
      val rat2 = SmallRational.of(nextLong(-1000L, 1000L), nextLong(1L, 100L))
      val d1 = rat1.toDouble()
      val d2 = rat2.toDouble()

      assertThat(closeEnough((rat1 + rat2).toDouble(), d1 + d2, 1e-11)).isTrue()
      assertThat(closeEnough((rat1 - rat2).toDouble(), d1 - d2, 1e-11)).isTrue()
      assertThat(closeEnough((rat1 * rat2).toDouble(), d1 * d2, 1e-12)).isTrue()
      if (rat2 != ZERO) {
        assertThat(closeEnough((rat1 / rat2).toDouble(), d1 / d2, 1e-12)).isTrue()
      }
    }
  }

  fun closeEnough(d1: Double, d2: Double, toler: Double): Boolean {
    if (d1.absoluteValue < toler && d2.absoluteValue < toler) return true
    val ratio = d1 / d2
    val b = ratio > 1.0 - toler && ratio < 1.0 + toler
    return b
  }
}
