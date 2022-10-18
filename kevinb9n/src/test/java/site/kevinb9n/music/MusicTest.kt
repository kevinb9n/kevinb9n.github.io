package site.kevinb9n.music

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.music.Accidental.DOUBLE_FLAT
import site.kevinb9n.music.Accidental.FLAT
import site.kevinb9n.music.Accidental.SHARP
import site.kevinb9n.music.PitchName.*

class MusicTest {
  @Test
  fun testEasy() {
    val cs = ChordSpelling(TriadFlavor.MAJOR, PitchClassSpelling(C))
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(C),
      PitchClassSpelling(E),
      PitchClassSpelling(G),
    )
  }

  @Test
  fun testWeird1() {
    val cs = ChordSpelling(TetradFlavor.DIMINISHED7, PitchClassSpelling(G, FLAT))
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(G, FLAT),
      PitchClassSpelling(B, DOUBLE_FLAT),
      PitchClassSpelling(D, DOUBLE_FLAT),
      PitchClassSpelling(F, DOUBLE_FLAT), // well now that's fookin weird
    )
  }

  @Test
  fun testWeird2() {
    val cs = ChordSpelling(TetradFlavor.DIMINISHED7, PitchClassSpelling(F, SHARP))
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(F, SHARP),
      PitchClassSpelling(A),
      PitchClassSpelling(C),
      PitchClassSpelling(E, FLAT),
    )
  }
}
