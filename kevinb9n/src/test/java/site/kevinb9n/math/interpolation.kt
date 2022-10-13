package site.kevinb9n.math

interface Interpolatr {
  fun interpolate(d: Double): Double
}

fun interpolator(earlyEagerness: Double, lateEagerness: Double): Interpolatr {
  require(earlyEagerness > 0.0 && earlyEagerness < 1.0) { earlyEagerness }
  require(lateEagerness > 0.0 && lateEagerness < 1.0) { lateEagerness }

  // If both are 0.5 we want the trivial interpolation y = x
  // Imagine two lines perpendicular to that, cutting its sqrt(2) length into thirds
  // these lines would be y = -x + 2/3 and y = -x + 4/3
  // their domains would be [0, 2/3] and [1/3, 1] respectively (decr. eagerness order)
  // Each would be 2rad2/3 units long

  val x1: Double = (1.0 - earlyEagerness) * 2.0 / 3.0
  val y1: Double = (0.0 + earlyEagerness) * 2.0 / 3.0

  val x2: Double = (1.5 - lateEagerness) * 2.0 / 3.0
  val y2: Double = (0.5 + lateEagerness) * 2.0 / 3.0

  val mci = MonotoneCubicInterpolator.of(
    doubleArrayOf(0.0, x1, x2, 1.0),
    doubleArrayOf(0.0, y1, y2, 1.0))
  return object : Interpolatr {
    override fun interpolate(d: Double) = mci.applyAsDouble(d)
  }
}
