package site.kevinb9n.plane

import kotlin.math.abs

data class Triangle(val leg1: Vector, val leg2: Vector) {
  val leg3 = -(leg1 + leg2)

  fun signedArea() = leg1.crossMag(leg2) / 2.0
  fun area() = abs(signedArea())

  fun normalize() = if (leg1.crossMag(leg2) >= 0) this else Triangle(-leg3, -leg2)
}
