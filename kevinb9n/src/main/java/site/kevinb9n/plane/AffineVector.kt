package site.kevinb9n.plane

interface AffineVector<P: AffinePoint<P, V>, V : AffineVector<P, V>> {
  val magnitude: Double

  // Useless or senseless: unaryPlus contains set invoke not
  // Debatable: inc/dec could affect the magnitude but meh
  // Good only for 1D: compareTo/rangeTo
  // Only for integral types: rem
  // Might make sense for n-dimensional vector: get
  // Automatically handled: *Assign

  operator fun plus(that: V): V
  operator fun plus(point: P): P
  operator fun times(scalar: Double): V
  operator fun div(scalar: Double): V

  // times(V) would be ambiguous between dot/cross product...

  operator fun unaryMinus(): V = this * -1.0
  operator fun minus(other: V): V = this + -other
}

// Make scalar multiplication commutative or whatever
operator fun <P: AffinePoint<P, V>, V: AffineVector<P, V>> Double.times(v: V): V = v * this
