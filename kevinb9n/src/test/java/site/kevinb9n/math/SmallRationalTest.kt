package site.kevinb9n.math

import com.google.common.truth.Truth.assertWithMessage
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random.Default.nextInt

class SmallRationalTest {
  @Test
  fun fuck() {
    val limit = 33_000
    var worstPlus = 0.0
    var worstMinus = 0.0
    var worstTimes = 0.0
    var worstDiv = 0.0

    for (i in 1..1_000_000) {
      val rat1 = SmallRational.of(nextInt(-limit, limit), nextInt(1, limit))
      val rat2 = SmallRational.of(nextInt(-limit, limit), nextInt(1, limit))
      val d1 = rat1.toDouble()
      val d2 = rat2.toDouble()

      worstPlus = maxOf(worstPlus, discrep(rat1 + rat2, d1 + d2))
      worstMinus = maxOf(worstMinus, discrep(rat1 - rat2, d1 - d2))
      worstTimes = maxOf(worstTimes, discrep(rat1 * rat2, d1 * d2))
      if (rat2 == SmallRational.of(0)) continue
      worstDiv = maxOf(worstDiv, discrep(rat1 / rat2, d1 / d2))
//      assertWithMessage("$rat1 + $rat2").that(closeEnough((rat1 + rat2).toDouble(), d1 + d2, 1e-10)).isTrue()
//      assertWithMessage("$rat1 - $rat2").that(closeEnough((rat1 - rat2).toDouble(), d1 - d2, 1e-10)).isTrue()
//      assertWithMessage("$rat1 * $rat2").that(closeEnough((rat1 * rat2).toDouble(), d1 * d2, 1e-10)).isTrue()
//      if (rat2 == of(0)) continue
//      assertWithMessage("$rat1 / ($rat2)").that(closeEnough((rat1 / rat2).toDouble(), d1 / d2, 1e-10)).isTrue()
    }
    println(worstPlus)
    println(worstMinus)
    println(worstTimes)
    println(worstDiv)
  }

  fun discrep(d1: SmallRational, d2: Double): Double {
    return abs(d1.toDouble() - d2)
  }

  fun closeEnough(d1: Double, d2: Double, toler: Double): Boolean {
    if (d1.absoluteValue < toler && d2.absoluteValue < toler) return true
    val discrep = (d1 - d2) / d2
    val b = discrep.absoluteValue < toler
    if (!b) println("$d1 != $d2")
    return b
  }
}
