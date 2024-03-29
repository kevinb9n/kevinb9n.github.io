package site.kevinb9n.plane

/**
 * A type having a difference function that returns a different type called a vector.
 * Supports operations: P - P = V, P + V = P, V - V = P
 * With all the properties you'd expect.
 */
interface AffinePoint<P : AffinePoint<P, V>, V : AffineVector<P, V>> {
  operator fun plus(that: V): P
  operator fun minus(that: P): V
  operator fun minus(that: V): P = this + -that

  // Useless or senseless: unaryPlus contains set invoke inc/dec compareTo/rangeTo
  // Need special zero value: unaryMinus not times/div/rem
  // Might make sense for n-dimensional point/vector: get
  // Automatically handled: *Assign

  /** Returns the point `m` for which `other - m == m - this` (as nearly as possible). */
  fun midpoint(other: P): P = this - (this - other) / 2.0

  /** Returns the point `r` for which `midpoint(r) == other` (as nearly as possible). */
  fun reflectAbout(other: P): P = other - (this - other)
}
