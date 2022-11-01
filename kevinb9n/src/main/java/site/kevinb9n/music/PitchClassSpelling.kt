package site.kevinb9n.music

/**
 * A "spelling" of a pitch class is *one* of the ("enharmonic") ways to identify that pitch class.
 * For example, "G𝄫", "F", and "E♯" are all spellings for the same pitch class.
 */
data class PitchClassSpelling(
        val letter: PitchLetter,
        val modifier: PitchModifier = PitchModifier.NATURAL) {

  fun pitchClass() = letter.mod(modifier)

  fun sharper() = copy(letter, modifier + 1)
  fun flatter() = copy(letter, modifier - 1)

  operator fun plus(interval: SimpleInterval): PitchClassSpelling {
    // Much easier to do the math in semitones then reverse-engineer the modifier
    val newPitchClass = pitchClass() + interval
    // The difference between F♯ and G♭ matters: choose the letter from the interval size
    val newLetter = letter + interval.size
    val newModifier = PitchModifier.sharps(newPitchClass - newLetter.natural())
    return PitchClassSpelling(newLetter, newModifier)
  }

  override fun toString() = "$letter$modifier"
}
