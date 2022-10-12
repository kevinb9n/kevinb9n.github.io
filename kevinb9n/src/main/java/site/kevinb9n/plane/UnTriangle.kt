package site.kevinb9n.plane

import site.kevinb9n.plane.Vector.Companion.vector

// Goal is that == means congruent, and similar only has to check ratios
// An ordered pair of vectors where the first vector is always at angle 0 and the
// second always at angle 0-180 should do it
// that means we need to normalize
// the three legs are vectors that sum to 0
// standardize vertex order: make signed area non-negative
// standardize choice of 2 vectors: leave out the longest side
// standardize rotation: leg1 should always be at angle 0, and leg2 at angle 0-180
// and the largest angle is special, it's the only one that can be right or obtuse

data class UnTriangle(val leg1: Vector, val leg2: Vector): ConvexPolygon {
  // the angle between leg1 and leg2 is not leg1.angleWith(leg2), but HALF_TURN - that.
  // should I rethink the representation??
  init {
    require(leg1.isHorizontal())
    require(leg2.y >= 0.0)
    if (leg2.isHorizontal()) {
      require(leg2.x >= leg1.x)
      require(leg1.x >= 0.0)
    } else {
      val leg3mag = (leg1 + leg2).magnitude
      require(leg1.magnitude <= leg3mag)
      require(leg2.magnitude <= leg3mag)
    }
  }

  override fun area() = leg1.crossMag(leg2) / 2.0

  val leg3 = -(leg1 + leg2)

  override fun similar(other: ClosedShape): Boolean {
    return other is UnTriangle
      && closeEnough(leg2.direction.degrees, other.leg2.direction.degrees)
      && closeEnough(leg2.magnitude * other.leg1.magnitude, leg1.magnitude * other.leg2.magnitude)
  }

  fun isEquilateral() = closeEnough(leg1.magnitude, leg2.magnitude)
    && closeEnough(leg1.magnitude, leg3.magnitude)

//   this is the vector from leg1's start point to the centroid
  fun centroid() = Vector.mean(leg1, leg1, leg2)

  companion object {
    fun triangle(a: Point, b: Point, c: Point) = triangle(b - a, c - b)
    // counter clockwise order
    fun triangle(aLen: Double, bLen: Double, cLen: Double): UnTriangle {
      val list = listOf(aLen, bLen, cLen)
      val max : Int = (0..2).maxByOrNull { list[it] }!!
      val leg1 = cyclicGet(list, max - 2)
      val leg2 = cyclicGet(list, max - 1)
      val leg3 = cyclicGet(list, max)

      assert(leg3 >= leg1)
      assert(leg3 >= leg2)
      require(leg1 + leg2 >= leg3)

      val a = Angle.acos((leg1 * leg1 + leg2 * leg2 - leg3 * leg3) / (2 * leg1 * leg2))
      return triangle(vector(leg1, 0), vector(magnitude = leg2, direction = Angle.HALF_TURN - a))
    }
    fun triangle(a: Vector, b: Vector): UnTriangle {
      if (a.collinear(b)) {
        val list = listOf(a.magnitude, b.magnitude, (a + b).magnitude).sorted()
        return UnTriangle(vector(list[0], 0), vector(list[1], 0))
      }
      val legs = if (a.isLeftTurn(b)) {
        listOf(a, b, -(a + b))
      } else {
        listOf(-b, -a, a + b)
      }
      val longest = (0 until 3).maxByOrNull { legs[it].magnitude }!!
      val leg1 = cyclicGet(legs, longest + 1)
      val leg2 = cyclicGet(legs, longest + 2)
      val angle = -leg1.direction
      return UnTriangle(leg1.rotate(angle), leg2.rotate(angle))
    }
  }
}
