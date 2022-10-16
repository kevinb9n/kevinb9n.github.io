package site.kevinb9n.math

import kotlin.math.abs
import kotlin.math.round

fun gcd(a: Int, b: Int): Int =
  if (b == 0) abs(a)
  else gcd(b, a.mod(b))

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
fun factors(value: Int): IntArray = (1 .. value).filter { value % it == 0 }.toIntArray()
fun mean(a: Double, b: Double) = (a + b) / 2.0
