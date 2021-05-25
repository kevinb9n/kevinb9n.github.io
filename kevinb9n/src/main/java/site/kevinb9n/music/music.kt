package site.kevinb9n.music

data class AbsoluteNote(val midiNumber: Int)


enum class Accidental(val semitoneDelta: Int, val notation: String, val asciified: String) {
  DOUBLE_FLAT(-2, "𝄫", "bb"),
  FLAT(-1, "♭", "b"),
  NATURAL(0, "", ""),  // ♮
  SHARP(1, "♯", "#"),
  DOUBLE_SHARP(2, "𝄪", "##"),
  ;

  companion object {
    fun fromAscii(s: String) = values().first { it.asciified == s }
  }
}
