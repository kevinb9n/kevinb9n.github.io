package site.kevinb9n.music

enum class Quality(private val semitonesFn: (SimpleIntervalSize) -> Int) {
  DIMINISHED({ it.kind.semitonesLower() - 1 }),
  MINOR({ (it.kind as Imperfect).semitonesMinor }),
  PERFECT({ (it.kind as Perfect).semitones }),
  MAJOR({ (it.kind as Imperfect).semitonesMajor }),
  AUGMENTED({ it.kind.semitonesHigher() + 1 }),
  ;

  /** The inverse of this quality. `-DIMINISHED == AUGMENTED, -MAJOR == MINOR, -PERFECT = PERFECT`. */
  operator fun unaryMinus() = enumValues<Quality>().reversed()[ordinal]

  operator fun get(interval: SimpleIntervalSize): SimpleInterval {
    return SimpleInterval(this, interval)
  }

  internal fun semitones(size: SimpleIntervalSize): Int {
    val semitones = semitonesFn(size)
    require(semitones in 0..12) { "$this $size $semitones" }
    return semitones
  }

  override fun toString() = name.lowercase().replace('_', ' ')
}
