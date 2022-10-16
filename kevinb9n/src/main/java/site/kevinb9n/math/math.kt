package site.kevinb9n.math

import kotlin.math.abs

fun gcd(a: Int, b: Int): Int =
  if (b == 0) abs(a)
  else gcd(b, a.mod(b))
