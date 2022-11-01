package site.kevinb9n.music

/** Note: comparison is not by semitones, but by size-then-quality. */
data class SimpleInterval(val quality: Quality = Quality.PERFECT, val size: SimpleIntervalSize)
    : Comparable<SimpleInterval> {
  init {
      require(semitones in 0..12)
  }

  val semitones: Int get() = quality.semitones(size)

  /** Returns the simple interval `inverse` such that `this + inverse == OCTAVE`. */
  operator fun unaryMinus() = SimpleInterval(-quality, -size)

  override fun compareTo(other: SimpleInterval) = siComparator.compare(this, other)
  override fun toString() = "$quality $size"

  companion object {
    fun allSimpleIntervals(): List<SimpleInterval> {
      return SimpleIntervalSize.values().flatMap { s ->
        Quality.values().flatMap { q ->
          try {
            listOf(q[s])
          } catch (e: Exception) {
            listOf()
          }
        }
      }.toList()
    }

    private val siComparator = compareBy<SimpleInterval> { it.size }.thenBy { it.quality }
  }
}
