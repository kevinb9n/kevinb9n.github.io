package site.kevinb9n.plane

/**
 * A type having a difference function that returns a different type called a vector.
 * Supports operations: P - P = V, P + V = P, V - V = P
 * With all the properties you'd expect.
 */
interface Point<P : Point<P, V>, V : Vector<P, V>> {
  // Useless or senseless: unaryPlus contains set invoke
  // Good for 1D: inc/dec compareTo/rangeTo
  // Need special zero value: unaryMinus not times/div/rem
  // Might make sense for n-dimensional point/vector: get
  // Automatically handled: *Assign

  operator fun plus(v: V): P
  operator fun minus(p: P): V
  operator fun minus(v: V): P = this + -v

  /** Returns the point `m` for which `other - m == m - this` (as nearly as possible). */
  fun midpoint(other: P): P = this + (other - this as P) / 2.0 // TODO: warning?

  /** Returns the point `r` for which `r - other == other - this` (as nearly as possible). */
  fun reflectAbout(other: P): P = other - this as P + other // TODO: warning?
}
