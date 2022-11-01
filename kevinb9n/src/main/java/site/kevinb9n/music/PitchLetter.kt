package site.kevinb9n.music

import site.kevinb9n.music.PitchClass.*
import site.kevinb9n.music.PitchLetter.*
import site.kevinb9n.music.PitchModifier.FLAT
import site.kevinb9n.music.PitchModifier.SHARP

/**
 * One of the 7 letters we form pitch spellings out of (A-G).
 */
enum class PitchLetter(private val naturalPitchClass: PitchClass) {
  A(INDIGO), B(MAGENTA), C(RED), D(ORANGE), E(YELLOW), F(SPRING), G(TEAL);

  /** `C + FOURTH == F, A + OCTAVE == A` */
  operator fun plus(size: SimpleIntervalSize) = cyclicPlus(this, size.ordinal)

  /** `C - FIFTH == F, A - OCTAVE == A` */
  operator fun minus(size: SimpleIntervalSize) = cyclicPlus(this, -size.ordinal)

  /** `C - F == FIFTH, A - A == UNISON` (will never return `OCTAVE`) */
  operator fun minus(that: PitchLetter) = cyclicMinus(this, that)

  fun natural() = naturalPitchClass
  fun mod(mod: PitchModifier) = natural() + mod.semitonesAdded
  fun sharp() = mod(SHARP)
  fun flat() = mod(FLAT)
}
