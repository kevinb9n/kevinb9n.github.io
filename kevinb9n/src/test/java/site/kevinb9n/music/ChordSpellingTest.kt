package site.kevinb9n.music

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.music.PitchModifier.*
import site.kevinb9n.music.PitchLetter.*
import site.kevinb9n.music.Quality.*
import site.kevinb9n.music.SimpleIntervalSize.*

class ChordSpellingTest {

  @Test fun testChordByNameSimple() {
    val cs = ChordSpelling(C)
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(C),
      PitchClassSpelling(E),
      PitchClassSpelling(G),
    )
  }

  @Test fun testChordByName2() {
    val cs = ChordSpelling(G, FLAT, DIMINISHED7)
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(G, FLAT),
      PitchClassSpelling(B, DOUBLE_FLAT),
      PitchClassSpelling(D, DOUBLE_FLAT),
      PitchClassSpelling(F, DOUBLE_FLAT),
    )
  }

  @Test fun testChordByName3() {
    val cs = ChordSpelling(F, SHARP, DIMINISHED7)
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(F, SHARP),
      PitchClassSpelling(A),
      PitchClassSpelling(C),
      PitchClassSpelling(E, FLAT),
    )
  }

  @Test fun testChordByName4() {
    val cs = ChordSpelling(A, SHARP, TRIAD_AUGMENTED).transpose(MAJOR[SECOND])
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(B, SHARP),
      PitchClassSpelling(D, DOUBLE_SHARP),
      PitchClassSpelling(F, TRIPLE_SHARP)
    )
  }

  @Test fun testChordByName5() {
    val cs = ChordSpelling(D, DOUBLE_FLAT, TRIAD_AUGMENTED)
    assertThat(cs.pitchClasses()).containsExactly(
      PitchClassSpelling(D, DOUBLE_FLAT),
      PitchClassSpelling(F, FLAT),
      PitchClassSpelling(A, FLAT)
    )
  }

  @Test fun testEquivalentsInversions() {
    assertSamePitches(ChordSpelling(C, PitchModifier.NATURAL, TRIAD_AUGMENTED), ChordSpelling(E, TRIAD_AUGMENTED))
    assertSamePitches(ChordSpelling(C, TRIAD_AUGMENTED), ChordSpelling(G, SHARP, TRIAD_AUGMENTED))
    assertSamePitches(ChordSpelling(A, DIMINISHED7), ChordSpelling(C, DIMINISHED7))
    assertSamePitches(ChordSpelling(A, DIMINISHED7), ChordSpelling(E, FLAT, DIMINISHED7))
    assertSamePitches(ChordSpelling(A, DIMINISHED7), ChordSpelling(F, SHARP, DIMINISHED7))

    // gonna change this API
    assertSamePitches(ChordSpelling(A, MINOR7), ChordSpelling(C).mod(ADD6))
  }

  fun assertSamePitches(vararg chords: ChordSpelling) {
    val pcs = chords[0].pitchClasses().map { it.pitchClass() }.toSet()
    for (c in chords) { // well the first one oughtta pass
      assertThat(c.pitchClasses().map(PitchClassSpelling::pitchClass))
        .containsExactlyElementsIn(pcs)
    }
  }
}
