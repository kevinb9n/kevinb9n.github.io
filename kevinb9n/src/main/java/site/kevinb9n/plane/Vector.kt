package site.kevinb9n.plane

interface Vector<P: Point<P, V>, V : Vector<P, V>> {
  val magnitude: Double

  // Useless or senseless: unaryPlus contains set invoke not
  // Debatable: inc/dec could affect the magnitude but meh
  // Good only for 1D: compareTo/rangeTo
  // Only for integral types: rem
  // Might make sense for n-dimensional vector: get
  // Automatically handled: *Assign

  operator fun plus(other: V): V
  operator fun plus(point: P): P
  operator fun times(scalar: Number): V
  operator fun div(scalar: Number): V

  // times(V) would be ambiguous between dot/cross product...

  operator fun unaryMinus(): V = this * -1.0
  operator fun minus(other: V): V = this + -other
}

// Make scalar multiplication commutative or whatever
operator fun <P: Point<P, V>, V: Vector<P, V>> Number.times(v: V): V = v * this
