package site.kevinb9n.music

import site.kevinb9n.music.PitchModifier.NATURAL
import site.kevinb9n.music.Quality.*
import site.kevinb9n.music.SimpleIntervalSize.*

val TRITONE = AUGMENTED[FOURTH]

data class ScaleOrChordType(val intervals: List<SimpleInterval>) {
  constructor(vararg intervals: SimpleInterval) : this(intervals.toList())
  fun withRoot(root: PitchClassSpelling) =
    ScaleOrChord(listOf(root) + intervals.map { root + it })

  fun add(interval: SimpleInterval) = copy((intervals + interval).sorted())
  fun remove(interval: SimpleInterval) = copy((intervals.filterNot { it == interval }))
}

data class ScaleOrChord(val pitches: List<PitchClassSpelling>) {
  constructor(vararg pitches: PitchClassSpelling) : this(pitches.toList())
}

val TRIAD_DIMINISHED = ScaleOrChordType(MINOR[THIRD], DIMINISHED[FIFTH])
val TRIAD_MINOR = ScaleOrChordType(MINOR[THIRD], PERFECT[FIFTH])
val TRIAD_MAJOR = ScaleOrChordType(MAJOR[THIRD], PERFECT[FIFTH])
val TRIAD_AUGMENTED = ScaleOrChordType(MAJOR[THIRD], AUGMENTED[FIFTH])
val TRIAD_SUS2 = ScaleOrChordType(MAJOR[SECOND], PERFECT[FIFTH])
val TRIAD_SUS4 = ScaleOrChordType(PERFECT[FOURTH], PERFECT[FIFTH])

data class ChordMod(val add: Boolean, val interval: SimpleInterval) {}

private val ADD_DIM7 = ChordMod(true, DIMINISHED[SEVENTH])
private val ADD_MINOR7 = ChordMod(true, MINOR[SEVENTH])
private val ADD_MAJOR7 = ChordMod(true, MAJOR[SEVENTH])

val DOMINANT7 = TRIAD_MAJOR.add(MINOR[SEVENTH])
val MAJOR7 = TRIAD_MAJOR.add(MAJOR[SEVENTH])
val MINOR7 = TRIAD_MINOR.add(MINOR[SEVENTH])
val MINOR_MAJOR7 = TRIAD_MINOR.add(MAJOR[SEVENTH])
val DIMINISHED7 = TRIAD_DIMINISHED.add(DIMINISHED[SEVENTH])
val HALF_DIMINISHED7 = TRIAD_DIMINISHED.add(MINOR[SEVENTH])
val SEVENTH_SUS4 = TRIAD_SUS4.add(MINOR[SEVENTH])

// val SIX_NINE =


val ADD_FLAT6 = ChordMod(true, MINOR[SIXTH])
val ADD6 = ChordMod(true, MAJOR[SIXTH])
val ADD_FLAT9 = ChordMod(true, MINOR[SECOND])
val ADD9 = ChordMod(true, MAJOR[SECOND])
val ADD_SHARP9 = ChordMod(true, AUGMENTED[SECOND])
val ADD11 = ChordMod(true, PERFECT[FOURTH])
val NO3 = ChordMod(false, MAJOR[THIRD])
val NO5 = ChordMod(false, PERFECT[FIFTH])

data class ChordSpelling(val root: PitchClassSpelling, val type: ScaleOrChordType = TRIAD_MAJOR) {
  constructor(
    letter: PitchLetter,
    modifier: PitchModifier = NATURAL,
    flavor: ScaleOrChordType = TRIAD_MAJOR) :
      this(PitchClassSpelling(letter, modifier), flavor)

  constructor(letter: PitchLetter, flavor: ScaleOrChordType) : this(letter, NATURAL, flavor)

  fun pitchClasses(): List<PitchClassSpelling> = listOf(root) + type.intervals.map { root + it }
  fun mod(mod: ChordMod) =
    if (mod.add) {
      copy(root, type.add(mod.interval))
    } else {
      copy(root, type.add(mod.interval))
    }

  fun transpose(interval: SimpleInterval) = copy(root + interval)
  fun transpose(quality: Quality, size: SimpleIntervalSize) = transpose(quality[size])
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
