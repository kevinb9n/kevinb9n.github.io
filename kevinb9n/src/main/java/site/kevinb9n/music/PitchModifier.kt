package site.kevinb9n.music

enum class PitchModifier(val suffix: String, val semitonesAdded: Int) {
  TRIPLE_FLAT("𝄫♭", -3),
  DOUBLE_FLAT("𝄫", -2),
  FLAT("♭", -1),
  NATURAL("", 0),
  SHARP("♯", 1),
  DOUBLE_SHARP("𝄪", 2),
  TRIPLE_SHARP("𝄪♯", 3);

  companion object {
    /**
     * Returns the `PitchModifier` representing the given number of sharps; or, if `sharps` is
     * negative, the appropriate number of flats.
     */
    fun sharps(sharps: Int) = enumValues<PitchModifier>()[NATURAL.ordinal + sharps]

    /**
     * Returns the `PitchModifier` representing the given number of flats; or, if `flats` is
     * negative, the appropriate number of sharps.
     */
    fun flats(flats: Int) = sharps(-flats)
  }

  override fun toString() = suffix
  operator fun unaryMinus() = enumValues<PitchModifier>().reversed()[ordinal]
  operator fun plus(semitones: Int) = enumValues<PitchModifier>()[ordinal + semitones]
  operator fun minus(semitones: Int) = plus(-semitones)
}
