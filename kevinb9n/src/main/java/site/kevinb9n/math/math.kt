package site.kevinb9n.math

import java.lang.Math.pow
import java.lang.Math.rint
import java.lang.Math.scalb
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sqrt

fun gcd(a: Int, b: Int): Int =
  if (b == 0) abs(a)
  else gcd(b, a.mod(b))

val PHI = mean(1.0, sqrt(5.0))
fun fib(n: Int) = round(pow(PHI, n.toDouble()) / sqrt(5.0))

fun closeEnough(d1: Number, d2: Number) = distance(d1.toDouble(), d2.toDouble()) < 1e-15

fun fixNearInteger(d: Double, toler: Double): Double {
  val nearest = round(d)
  return if (distance(d, nearest) < toler) {
    if (nearest == -0.0) 0.0 else nearest
  } else {
    d
  }
}

fun distance(a: Double, b: Double) = abs(a - b)

fun Double.modWithMinimum(modulus: Double, minimum: Double) = minimum + (this - minimum).mod(modulus)
fun Int.modWithMinimum(modulus: Int, minimum: Int) = minimum + (this - minimum).mod(modulus)

fun sumProduct(list1: List<Double>, list2: List<Double>) = list1.zip(list2) { a, b -> a * b }.sum()
fun factors(value: Int): List<Int> = (1 .. value).filter { value % it == 0 }
fun mean(a: Double, b: Double) = a + (b - a) * 0.5
fun geometricMean(a: Double, b: Double) = sqrt(a * b)

fun roundToBinaryDecimalPlaces(unrounded: Double, places: Int) =
  scalb(rint(scalb(unrounded, places)), -places)

fun sineTo63Digits(input: BigDecimal): BigDecimal {
  // sin(x) = x^1/1! - x^3/3! + x^5/5! - x^7/7! + ...

  val xsquared = input * input
  var counter = BigDecimal.ONE
  var nextTerm = input
  var result = BigDecimal.ZERO

  while (nextTerm.compareTo(BigDecimal.ZERO) != 0) {
    result += nextTerm
    nextTerm *= -xsquared
    var div = ++counter
    div *= ++counter
    nextTerm = nextTerm.divide(div, 120, HALF_EVEN)
  }
  return result.setScale(63, HALF_EVEN).stripTrailingZeros()
}
