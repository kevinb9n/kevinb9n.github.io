package site.kevinb9n.music

import com.google.common.math.IntMath.mod
import site.kevinb9n.math.modWithMinimum
import site.kevinb9n.music.PitchClass.PC00
import site.kevinb9n.music.PitchClass.PC02
import site.kevinb9n.music.PitchClass.PC03
import site.kevinb9n.music.PitchClass.PC05
import site.kevinb9n.music.PitchClass.PC07
import site.kevinb9n.music.PitchClass.PC09
import site.kevinb9n.music.PitchClass.PC10
import site.kevinb9n.music.SimpleIntervalSize.FIFTH
import site.kevinb9n.music.SimpleIntervalSize.FOURTH
import site.kevinb9n.music.SimpleIntervalSize.SECOND
import site.kevinb9n.music.SimpleIntervalSize.SEVENTH
import site.kevinb9n.music.SimpleIntervalSize.THIRD

/**
 * One of the 12 denotable pitches (without octave) that form the basis of our "12-tone system". We
 * name them using integers from 0 to 11 because each has multiple equivalent "spellings". For
 * example, the same single pitch class could be spelled "C♯", "D♭", "B𝄪", "E𝄫♭", etc., and
 * none is more correct than the others.
 *
 * Pitch class 0 is not special! They are cyclically ordered. Pitch class 03 is 2 steps (so-called
 * "half-steps") "higher" than pitch class 01, but it is 10 steps "lower" than it as well.
 */
enum class PitchClass {
  PC00, PC01, PC02, PC03, PC04, PC05, PC06, PC07, PC08, PC09, PC10, PC11;

  operator fun plus(increment: Int) = cyclicPlus(this, increment)
  operator fun minus(decrement: Int) = plus(-decrement)
  operator fun minus(other: PitchClass) = cyclicMinus(this, other)
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
// val CONCERT = Reference(AbsolutePitch(PitchName.A.pc, 4), Frequency(440.0))

enum class PitchName(val pc: PitchClass) {
  A(PC07), B(PC09), C(PC10), D(PC00), E(PC02), F(PC03), G(PC05);

  operator fun plus(offset: Int) = cyclicPlus(this, offset)
  operator fun plus(size: SimpleIntervalSize) = plus(size.ordinal)
  operator fun minus(offset: Int) = plus(-offset)
  operator fun minus(size: SimpleIntervalSize) = minus(size.ordinal)
  operator fun minus(pitchName: PitchName) = cyclicMinus(this, pitchName)
}

enum class Accidental(val suffix: String, val offsetSemitones: Int) {
  TRIPLE_FLAT("♭𝄫", -3),
  DOUBLE_FLAT("𝄫", -2),
  FLAT("♭", -1),
  NATURAL("", 0),
  SHARP("♯", 1),
  DOUBLE_SHARP("𝄪", 2),
  TRIPLE_SHARP("♯𝄪", 3);

  companion object {
    fun flats(flats: Int) = enumValues<Accidental>()[3 - flats]
    fun sharps(sharps: Int) = enumValues<Accidental>()[3 + sharps]
  }

  override fun toString() = suffix
  operator fun plus(sharps: Int) = enumValues<Accidental>()[ordinal + sharps]
  operator fun minus(flats: Int) = plus(-flats)
}

enum class SimpleIntervalSize(val semitonesLower: Int, val semitonesHigher: Int) {
  UNISON(0, 0), SECOND(1, 2), THIRD(3, 4), FOURTH(5, 5),
  FIFTH(7, 7), SIXTH(8, 9), SEVENTH(10, 11), OCTAVE(12, 12);

  operator fun unaryMinus() = enumValues<SimpleIntervalSize>()[7 - ordinal]
  val isPerfect: Boolean get() = (semitonesLower == semitonesHigher)

  val semitonesMinor: Int
    get() {
      require(!isPerfect)
      return semitonesLower
    }

  val semitonesMajor: Int
    get() {
      require(!isPerfect)
      return semitonesHigher
    }

  val semitonesPerfect: Int
    get() {
      require(isPerfect)
      return semitonesLower
    }
}

enum class IntervalQuality {
  TWICE_DIMINISHED {
    override fun semitones(size: SimpleIntervalSize) = size.semitonesLower - 2
  },
  DIMINISHED {
    override fun semitones(size: SimpleIntervalSize) = size.semitonesLower - 1
  },
  MINOR {
    override fun semitones(size: SimpleIntervalSize) = size.semitonesMinor
  },
  PERFECT {
    override fun semitones(size: SimpleIntervalSize) = size.semitonesPerfect
  },
  MAJOR {
    override fun semitones(size: SimpleIntervalSize) = size.semitonesMajor
  },
  AUGMENTED {
    override fun semitones(size: SimpleIntervalSize) = size.semitonesHigher + 1
  },
  TWICE_AUGMENTED {
    override fun semitones(size: SimpleIntervalSize) = size.semitonesHigher + 2
  },
  ;

  operator fun unaryMinus() = enumValues<IntervalQuality>()[6 - ordinal]
  operator fun plus(size: SimpleIntervalSize) = SimpleInterval(size, this)
  abstract fun semitones(size: SimpleIntervalSize): Int
}

data class SimpleInterval(val size: SimpleIntervalSize, val quality: IntervalQuality = IntervalQuality.PERFECT) {
  init {
    require(semitones in 0..12)
  }

  val semitones: Int get() = quality.semitones(size)
  operator fun unaryMinus() = SimpleInterval(-size, -quality)
}

data class PitchClassSpelling(
  val name: PitchName,
  val modifier: Accidental = Accidental.NATURAL) {
  fun pitchClass() = name.pc + modifier.offsetSemitones
  override fun toString() = "$name$modifier"

  operator fun plus(interval: SimpleInterval): PitchClassSpelling {
    // roundabout?
    val newPc = pitchClass() + interval.semitones
    val newMod = Accidental.sharps((newPc - (name + interval.size).pc).modWithMinimum(12, -6))
    return PitchClassSpelling(name + interval.size, newMod)
  }
}

interface ChordFlavor {
  val intervals: List<SimpleInterval>
}

// names conflict with interval qualities
enum class TriadFlavor(override val intervals: List<SimpleInterval>) : ChordFlavor {
  DIMINISHED(listOf(IntervalQuality.MINOR + THIRD, IntervalQuality.DIMINISHED + FIFTH)),
  MINOR(listOf(IntervalQuality.MINOR + THIRD, IntervalQuality.PERFECT + FIFTH)),
  MAJOR(listOf(IntervalQuality.MAJOR + THIRD, IntervalQuality.PERFECT + FIFTH)),
  AUGMENTED(listOf(IntervalQuality.MAJOR + THIRD, IntervalQuality.AUGMENTED + FIFTH)),
  NO_THIRD(listOf(IntervalQuality.PERFECT + FIFTH)),
  SUS2(listOf(IntervalQuality.MAJOR + SECOND, IntervalQuality.PERFECT + FIFTH)),
  SUS4(listOf(IntervalQuality.PERFECT + FOURTH, IntervalQuality.PERFECT + FIFTH)),
  ;
}

enum class TetradFlavor(val triad: TriadFlavor, val seventh: IntervalQuality) : ChordFlavor {
  DOMINANT7(TriadFlavor.MAJOR, IntervalQuality.MINOR),
  MAJOR7(TriadFlavor.MAJOR, IntervalQuality.MAJOR),
  MINOR7(TriadFlavor.MINOR, IntervalQuality.MINOR),
  MINOR_MAJOR7(TriadFlavor.MINOR, IntervalQuality.MAJOR),
  DIMINISHED7(TriadFlavor.DIMINISHED, IntervalQuality.DIMINISHED),
  HALF_DIMINISHED7(TriadFlavor.DIMINISHED, IntervalQuality.MINOR),
  SEVENTH_SUS4(TriadFlavor.SUS4, IntervalQuality.MINOR),
  ;

  override val intervals get() = triad.intervals + SimpleInterval(SEVENTH, seventh)
}

data class ChordSpelling(val flavor: ChordFlavor, val root: PitchClassSpelling) {
  fun pitchClasses(): List<PitchClassSpelling> = listOf(root) + flavor.intervals.map { root + it }
}

inline fun <reified T : Enum<T>> cyclicPlus(start: T, distance: Int): T {
  val enumValues = enumValues<T>()
  return enumValues[mod(start.ordinal + distance, enumValues.size)]
}

inline fun <reified T : Enum<T>> cyclicMinus(start: T, end: T) =
  mod(start.ordinal - end.ordinal, enumValues<T>().size)
