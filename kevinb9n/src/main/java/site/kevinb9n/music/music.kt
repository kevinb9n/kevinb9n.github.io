package site.kevinb9n.music

import site.kevinb9n.math.modWithMinimum
import site.kevinb9n.music.Accidental.NATURAL
import site.kevinb9n.music.PitchClass.*
import site.kevinb9n.music.PitchLetter.*
import site.kevinb9n.music.Quality.*
import site.kevinb9n.music.SimpleIntervalSize.*
import site.kevinb9n.music.TriadFlavor.*

/**
 * One of the 12 denotable pitches (without octave) that form the basis of our "12-tone system".
 * You never actually interact with these directly and I've cheekily named them after colors. They
 * don't really have names, since multiple enharmonic "spellings" are equally correct for each.
 *
 * Careful: there is absolutely no significance to our having placed ORANGE as the first. They are
 * cyclically ordered. BLUE is 2 pitch classes greater than GREEN, but it's equally true to say
 * it's 10 pitch classes *less* than it.
 *
 * This depends on nothing else and nothing very useful can be done with it by itself.
 */
enum class PitchClass {
  ORANGE, MAC, YELLOW, SPRING, GREEN, TEAL, BLUE, INDIGO, VIOLET, MAGENTA, RED, BRICK;

  /**
   * Returns the `increment`th pitch class from this one, counting upward.
   *
   * * `pc + 0 == pc`
   * * `pc + 13 == pc + 1`
   * * `pc + 7 == pc - 5`
   */
  operator fun plus(increment: Int) = cyclicPlus(this, increment)

  operator fun minus(decrement: Int) = plus(-decrement)

  /** Returns the unique number `d` in the range -6..5 such that `that + d == this`. */
  operator fun minus(that: PitchClass) = (this.ordinal - that.ordinal).modWithMinimum(12, -6)
}

/**
 * One of the 7 letters we form pitch spellings out of (A-G).
 */
enum class PitchLetter {
  A, B, C, D, E, F, G;

  /** C + 3 == C + 10 == F */
  operator fun plus(offset: Int) = cyclicPlus(this, offset)

  /** C + FOURTH == F */
  operator fun plus(size: SimpleIntervalSize) = plus(size.ordinal)

  /** C - 4 == F */
  operator fun minus(offset: Int) = plus(-offset)

  /** C - FIFTH == F */
  operator fun minus(size: SimpleIntervalSize) = minus(size.ordinal)

  /** C - F == FIFTH */
  operator fun minus(that: PitchLetter) = cyclicMinus(this, that)
}

enum class Accidental(val suffix: String, val offsetSemitones: Int) {
  TRIPLE_FLAT("♭♭♭", -3),
  DOUBLE_FLAT("♭♭", -2),
  FLAT("♭", -1),
  NATURAL("", 0),
  SHARP("♯", 1),
  DOUBLE_SHARP("♯♯", 2),
  TRIPLE_SHARP("♯♯♯", 3);

  companion object {
    fun flats(flats: Int) = enumValues<Accidental>()[3 - flats]
    fun sharps(sharps: Int) = enumValues<Accidental>()[3 + sharps]
  }

  override fun toString() = suffix
  operator fun plus(sharps: Int) = enumValues<Accidental>()[ordinal + sharps]
  operator fun minus(flats: Int) = plus(-flats)
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

enum class Quality(private val semitonesFn: (SimpleIntervalSize) -> Int) {
  DIMINISHED({ it.kind.semitonesLower() - 1 }),
  MINOR({ (it.kind as Imperfect).semitonesMinor }),
  PERFECT({ (it.kind as Perfect).semitones }),
  MAJOR({ (it.kind as Imperfect).semitonesMajor }),
  AUGMENTED({ it.kind.semitonesHigher() + 1 }),
  ;

  /** The inverse of this quality. `-DIMINISHED == AUGMENTED, -MAJOR == MINOR, -PERFECT = PERFECT`. */
  operator fun unaryMinus() = enumValues<Quality>().reversed()[ordinal]

  fun semitones(size: SimpleIntervalSize): Int {
    val semitones = semitonesFn(size)
    require(semitones in 0..12) { "$this $size $semitones" }
    return semitones
  }

  override fun toString() = name.lowercase().replace('_', ' ')
}

data class SimpleInterval(val quality: Quality = PERFECT, val size: SimpleIntervalSize) {
  init { require(semitones in 0..12) }

  val semitones: Int get() = quality.semitones(size)

  /** Returns the simple interval `inverse` such that `this + inverse == OCTAVE`. */
  operator fun unaryMinus() = interval(-quality, -size)

  override fun toString() = "$quality $size"
}

fun allSimpleIntervals(): List<SimpleInterval> {
  return SimpleIntervalSize.values().flatMap { s->
    Quality.values().flatMap { q ->
      try {
        listOf(interval(q, s))
      } catch (e: Exception) {
        listOf()
      }
    }
  }.toList()
}

val TRITONE = interval(AUGMENTED, FOURTH)

fun interval(quality: Quality = PERFECT, size: SimpleIntervalSize) = SimpleInterval(quality, size)

// Why not use boomwhacker colors
val letterToClass = mapOf(
  A to INDIGO, B to MAGENTA, C to RED, D to ORANGE, E to YELLOW, F to SPRING, G to TEAL);

/**
 * A "spelling" of a pitch class is *one* of the ("enharmonic") ways to identify that pitch class.
 * For example, "G𝄫", "F", and "E♯" are all spellings for the same pitch class.
 */
data class PitchClassSpelling(
  val letter: PitchLetter,
  val modifier: Accidental = NATURAL) {
  fun pitchClass() = letterToClass[letter]!! + modifier.offsetSemitones
  override fun toString() = "$letter$modifier"

  operator fun plus(interval: SimpleInterval): PitchClassSpelling {
    // Easiest way is to do the math in semitones then reverse-engineer the modifier
    val newPitchClass = pitchClass() + interval.semitones
    val newLetter = letter + interval.size
    val pitchClassIfNatural = letterToClass[newLetter]!!
    val newAccidental = Accidental.sharps(newPitchClass - pitchClassIfNatural)
    return PitchClassSpelling(newLetter, newAccidental)
  }
}

interface ChordFlavor {
  val intervals: List<SimpleInterval>
}

// names conflict with interval qualities
enum class TriadFlavor(override val intervals: List<SimpleInterval>) : ChordFlavor {
  TRIAD_DIMINISHED(MINOR, THIRD, DIMINISHED, FIFTH),
  TRIAD_MINOR(MINOR, THIRD, PERFECT, FIFTH),
  TRIAD_MAJOR(MAJOR, THIRD, PERFECT, FIFTH),
  TRIAD_AUGMENTED(MAJOR, THIRD, AUGMENTED, FIFTH),
  TRIAD_NO_THIRD(PERFECT, FIFTH),
  TRIAD_SUS2(MAJOR, SECOND, PERFECT, FIFTH),
  TRIAD_SUS4(PERFECT, FOURTH, PERFECT, FIFTH),
  ;

  constructor(a: SimpleInterval) : this(listOf(a))
  constructor(q: Quality, s: SimpleIntervalSize) : this(interval(q, s))
  constructor(a: SimpleInterval, b: SimpleInterval) : this(listOf(a, b))
  constructor(q: Quality, s: SimpleIntervalSize, q2: Quality, s2: SimpleIntervalSize)
    : this(interval(q, s), interval(q2, s2))
}

enum class TetradFlavor(triad: TriadFlavor, seventhQuality: Quality) : ChordFlavor {
  DOMINANT7(TRIAD_MAJOR, MINOR),
  MAJOR7(TRIAD_MAJOR, MAJOR),
  MINOR7(TRIAD_MINOR, MINOR),
  MINOR_MAJOR7(TRIAD_MINOR, MAJOR),
  DIMINISHED7(TRIAD_DIMINISHED, DIMINISHED),
  HALF_DIMINISHED7(TRIAD_DIMINISHED, MINOR),
  SEVENTH_SUS4(TRIAD_SUS4, MINOR),
  ;

  override val intervals = triad.intervals + interval(seventhQuality, SEVENTH)
}

enum class AddOn(val interval: SimpleInterval) {
  ADD_FLAT6(MINOR, SIXTH),
  ADD6(MAJOR, SIXTH),
  ADD_FLAT9(MINOR, SECOND),
  ADD9(MAJOR, SECOND),
  ADD_SHARP9(AUGMENTED, SECOND),
  ADD11(PERFECT, FOURTH),
  ;

  constructor(quality: Quality, size: SimpleIntervalSize) : this(interval(quality, size))
}

data class AddOnFlavor(val base: ChordFlavor, val added: AddOn): ChordFlavor {
  override val intervals = base.intervals + added.interval
}

data class ChordSpelling(val root: PitchClassSpelling, val flavor: ChordFlavor) {
  constructor(
    letter: PitchLetter,
    modifier: Accidental = NATURAL,
    flavor: ChordFlavor = TRIAD_MAJOR) :
      this(PitchClassSpelling(letter, modifier), flavor)
  constructor(letter: PitchLetter, flavor: ChordFlavor) : this(letter, NATURAL, flavor)
  fun pitchClasses(): List<PitchClassSpelling> = listOf(root) + flavor.intervals.map { root + it }
  fun transpose(interval: SimpleInterval) = copy(root + interval)
  fun transpose(quality: Quality, size: SimpleIntervalSize) = transpose(interval(quality, size))
}

// Not doing anything with these yet
// enum class Temperament { EQUAL }
// data class AbsolutePitch(val pitchClass: PitchClass, val octave: Int)
// data class Frequency(val hertz: Double)
// data class Tuning(val temperament: Temperament, val ref: Reference) {}
//  fun getFrequency(pitchSpelling: PitchClassSpelling): Frequency
//
// data class Reference(val absPitch: AbsolutePitch, val frequency: Frequency)
//
// val CONCERT = Reference(AbsolutePitch(PitchLetter.A.pc, 4), Frequency(440.0))
