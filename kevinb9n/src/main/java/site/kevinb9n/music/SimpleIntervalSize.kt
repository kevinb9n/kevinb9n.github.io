package site.kevinb9n.music

/**
 * The "size" of an interval is only the "number part", without the "quality" (like "major").
 * There are intervals of "ninth" and above, but *simple* intervals are octave or less.
 */
enum class SimpleIntervalSize(val kind: IntervalKind) {
  UNISON(Perfect(0)), SECOND(Imperfect(2)), THIRD(Imperfect(4)), FOURTH(Perfect(5)),
  FIFTH(Perfect(7)), SIXTH(Imperfect(9)), SEVENTH(Imperfect(11)), OCTAVE(Perfect(12));

  /**
   * Inverse of this interval. `-THIRD == SIXTH`; `-OCTAVE = UNISON`.
   */
  operator fun unaryMinus() = enumValues<SimpleIntervalSize>().reversed()[ordinal]

  operator fun plus(that: SimpleIntervalSize) = cyclicPlus(this, that.ordinal)
  operator fun minus(that: SimpleIntervalSize) = this + -that
  override fun toString() = name.lowercase()
}

interface IntervalKind {
  fun semitonesLower(): Int
  fun semitonesHigher(): Int
}

data class Perfect(val semitones: Int) : IntervalKind {
  override fun semitonesLower() = semitones
  override fun semitonesHigher() = semitones
}

data class Imperfect (val semitonesMajor: Int) : IntervalKind {
  val semitonesMinor = semitonesMajor - 1
  override fun semitonesLower() = semitonesMinor
  override fun semitonesHigher() = semitonesMajor
}
