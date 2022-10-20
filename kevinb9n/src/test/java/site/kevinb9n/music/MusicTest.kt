package site.kevinb9n.music

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.music.Accidental.DOUBLE_FLAT
import site.kevinb9n.music.Accidental.DOUBLE_SHARP
import site.kevinb9n.music.Accidental.FLAT
import site.kevinb9n.music.Accidental.SHARP
import site.kevinb9n.music.Accidental.TRIPLE_SHARP
import site.kevinb9n.music.AddOn.ADD6
import site.kevinb9n.music.PitchName.*
import site.kevinb9n.music.SimpleIntervalSize.SECOND
import site.kevinb9n.music.TetradFlavor.*
import site.kevinb9n.music.TriadFlavor.AUGMENTED

class MusicTest {
  @Test
  fun testEasy() {
    val cs = ChordSpelling(C)
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(C),
      PitchClassSpelling(E),
      PitchClassSpelling(G),
    )
  }

  @Test
  fun testWeird1() {
    val cs = ChordSpelling(G, FLAT, DIMINISHED7)
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(G, FLAT),
      PitchClassSpelling(B, DOUBLE_FLAT),
      PitchClassSpelling(D, DOUBLE_FLAT),
      PitchClassSpelling(F, DOUBLE_FLAT), // well now that's fookin weird
    )
  }

  @Test
  fun testWeird2() {
    val cs = ChordSpelling(F, SHARP, DIMINISHED7)
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(F, SHARP),
      PitchClassSpelling(A),
      PitchClassSpelling(C),
      PitchClassSpelling(E, FLAT),
    )
  }

  @Test
  fun testWeird3() {
    val cs = ChordSpelling(A, SHARP, AUGMENTED).transpose(SimpleInterval(Quality.MAJOR, SECOND))
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(B, SHARP),
      PitchClassSpelling(D, DOUBLE_SHARP),
      PitchClassSpelling(F, TRIPLE_SHARP)
    )
  }

  @Test
  fun testWeird4() {
    val cs = ChordSpelling(D, DOUBLE_FLAT, AUGMENTED)
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(D, DOUBLE_FLAT),
      PitchClassSpelling(F, FLAT),
      PitchClassSpelling(A, FLAT)
    )
  }

  @Test
  fun equivalents() {
    assertSamePitches(ChordSpelling(C, AUGMENTED), ChordSpelling(E, AUGMENTED))
    assertSamePitches(ChordSpelling(C, AUGMENTED), ChordSpelling(G, SHARP, AUGMENTED))
    assertSamePitches(ChordSpelling(A, DIMINISHED7), ChordSpelling(C, DIMINISHED7))
    assertSamePitches(ChordSpelling(A, DIMINISHED7), ChordSpelling(E, FLAT, DIMINISHED7))
    assertSamePitches(ChordSpelling(A, DIMINISHED7), ChordSpelling(F, SHARP, DIMINISHED7))
    assertSamePitches(ChordSpelling(C, AddOnFlavor(TriadFlavor.MAJOR, ADD6)), ChordSpelling(A, MINOR7))
  }

  fun assertSamePitches(vararg chords: ChordSpelling) {
    val pcs = chords[0].pitchClasses().map { it.pitchClass() }.toSet()
    for (c in chords) {
      assertThat(c.pitchClasses().map { it.pitchClass() }).containsExactlyElementsIn(pcs)
    }
  }
}
