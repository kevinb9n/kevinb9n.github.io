package site.kevinb9n.music

import site.kevinb9n.math.modWithMinimum

/**
 * One of the 12 denotable pitches (without octave) that form the basis of our "12-tone system".
 * You never actually interact with these directly and I've cheekily named them after colors. They
 * don't really have names, since multiple enharmonic "spellings" are equally correct for each.
 *
 * The unit of distance between pitch classes (in the range [0, 11]) is called *semitones*.
 *
 * Careful: there is absolutely no significance to which one is placed first below. I'll probably
 * rotate them every now and then just for the hell of it. They are cyclically ordered. VIOLET is 2
 * pitch classes greater than BLUE, but it's *equally* true that it's 10 pitch classes *less* than
 * it.
 */
enum class PitchClass {
  // don't tell, but these are really just the boomwhacker colors
  GREEN, TEAL, BLUE, INDIGO, VIOLET, MAGENTA, RED, BRICK, ORANGE, GOLD, YELLOW, SPRING;

  /**
   * Returns the pitch class `semitones` semitones above this one.
   *
   * * `pc + 0 == pc`
   * * `pc + 13 == pc + 1`
   * * `pc + 7 == pc - 5`
   */
  operator fun plus(semitones: Int) = cyclicPlus(this, semitones)

  operator fun plus(interval: SimpleInterval) = plus(interval.semitones)

  /**
   * Returns the pitch class `semitones` semitones below this one.
   *
   * * `pc - 0 == pc`
   * * `pc - 13 == pc - 1`
   * * `pc - 7 == pc + 5`
   */
  operator fun minus(semitones: Int) = plus(-semitones)

  operator fun minus(interval: SimpleInterval) = plus(-interval)

  /**
   * Returns the unique number of semitones, in the range `-6..5`, that can be added to `that` to
   * get `this`; guarantees that `that + s == this`.
   */
  operator fun minus(that: PitchClass) = (this.ordinal - that.ordinal).modWithMinimum(12, -6)
}
