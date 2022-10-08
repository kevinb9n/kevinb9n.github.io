package site.kevinb9n.plane

import kotlin.math.abs
import kotlin.math.round

fun Number.modWithMininum(modulus: Number, min: Number) =
  ((toDouble() - min.toDouble()) % modulus.toDouble()) + min.toDouble()

fun closeEnough(d1: Number, d2: Number) = distance(d1.toDouble(), d2.toDouble()) < 1e-12

fun fixNearInteger(d: Double, toler: Double): Double {
  val nearest = round(d)
  return if (distance(d, nearest) < toler) {
    if (nearest == -0.0) 0.0 else nearest
  } else {
    d
  }
}

fun distance(a: Double, b: Double) = abs(a - b)
