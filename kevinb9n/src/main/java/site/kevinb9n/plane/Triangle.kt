package site.kevinb9n.plane

// Goal is that == means congruent, and similar only has to check ratios
// An ordered pair of vectors where the first vector is always at angle 0 and the
// second always at angle 0-180 should do it
// that means we need to normalize
// the three legs are vectors that sum to 0
// standardize vertex order: make signed area nonnegative
// standardize choice of 2 vectors: leave out the longest side
// standardize rotation: leg1 should always be at angle 0, and leg2 at angle 0-180
// and the largest angle is special, it's the only one that can be right or obtuse

data class Triangle(val leg1: Vector, val leg2: Vector) {
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
  fun area() = leg1.crossMag(leg2) / 2.0

  companion object {
    fun triangle(a: Point, b: Point, c: Point) = triangle(b - a, c - b)
    fun triangle(a: Vector, b: Vector): Triangle {
      if (a.collinear(b)) {
        val list = listOf(a.magnitude, b.magnitude, (a + b).magnitude).sorted()
        return Triangle(CartesianVector(list[0], 0), CartesianVector(list[1], 0))
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
      return Triangle(leg1.rotate(angle), leg2.rotate(angle))
    }
  }
}

fun <T> cyclicGet(list: List<T>, i: Int): T {
  return list[i % list.size]
}
