package site.kevinb9n.music

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import site.kevinb9n.music.PitchLetter.*
import site.kevinb9n.music.Quality.*
import site.kevinb9n.music.SimpleInterval.Companion.allSimpleIntervals
import site.kevinb9n.music.SimpleIntervalSize.*

class IntervalsTest {
  @Test fun all() {
    val ints = listOf(
      PERFECT[UNISON], AUGMENTED[UNISON],
      DIMINISHED[SECOND], MINOR[SECOND], MAJOR[SECOND], AUGMENTED[SECOND],
      DIMINISHED[THIRD], MINOR[THIRD], MAJOR[THIRD], AUGMENTED[THIRD],
      DIMINISHED[FOURTH], PERFECT[FOURTH], AUGMENTED[FOURTH],
    )
    val theRest = ints.map { -it }.reversed()
    assertThat(allSimpleIntervals()).containsExactlyElementsIn(ints + theRest).inOrder()
  }

  @Test fun fuckup () {
    val c = PitchClassSpelling(C)
    val i3 = MAJOR[THIRD]
    val pcss = listOf(
      c,
      c + i3, // E
      c + -i3, // Ab
      c + i3 + i3, // G#
      c + -i3 + -i3 // Fb
    )
    for ((i, pcs) in pcss.withIndex()) {
      for ((k, int) in allSimpleIntervals().withIndex()) {
        try {
          val s = "${pcs + int}"
          println("$i,$k,$s,$pcs,$int")
        } catch (e: Exception) {
          println("$i,$k,X,X,X,${e.message}")
        }
      }
    }
  }

  @Test
  fun testUnison() {
    assertThrows<Exception> { DIMINISHED[UNISON] }
    assertThrows<Exception> { MINOR[UNISON] }
    assertThrows<Exception> { MAJOR[UNISON] }

    val pu = PERFECT[UNISON]
    assertThat(pu.semitones).isEqualTo(0)
    assertThat(-pu).isEqualTo(PERFECT[OCTAVE])

    val au = AUGMENTED[UNISON]
    assertThat(au.semitones).isEqualTo(1)
    assertThat(-au).isEqualTo(DIMINISHED[OCTAVE])
  }

  @Test
  fun testSecond() {
    assertThrows<Exception> { PERFECT[SECOND] }

    val ds = DIMINISHED[SECOND]
    assertThat(ds.semitones).isEqualTo(0)
    assertThat(-ds).isEqualTo(AUGMENTED[SEVENTH])

    val mis = MINOR[SECOND]
    assertThat(mis.semitones).isEqualTo(1)
    assertThat(-mis).isEqualTo(MAJOR[SEVENTH])

    val mas = MAJOR[SECOND]
    assertThat(mas.semitones).isEqualTo(2)
    assertThat(-mas).isEqualTo(MINOR[SEVENTH])

    val aus = AUGMENTED[SECOND]
    assertThat(aus.semitones).isEqualTo(3)
    assertThat(-aus).isEqualTo(DIMINISHED[SEVENTH])
  }

  @Test
  fun testThird() {
    assertThrows<Exception> { PERFECT[THIRD] }

    val dt = DIMINISHED[THIRD]
    assertThat(dt.semitones).isEqualTo(2)
    assertThat(-dt).isEqualTo(AUGMENTED[SIXTH])

    val mit = MINOR[THIRD]
    assertThat(mit.semitones).isEqualTo(3)
    assertThat(-mit).isEqualTo(MAJOR[SIXTH])

    val mat = MAJOR[THIRD]
    assertThat(mat.semitones).isEqualTo(4)
    assertThat(-mat).isEqualTo(MINOR[SIXTH])

    val at = AUGMENTED[THIRD]
    assertThat(at.semitones).isEqualTo(5)
    assertThat(-at).isEqualTo(DIMINISHED[SIXTH])
  }

  @Test
  fun testFourth() {
    assertThrows<Exception> { MINOR[FOURTH] }
    assertThrows<Exception> { MAJOR[FOURTH] }

    val df = DIMINISHED[FOURTH]
    assertThat(df.semitones).isEqualTo(4)
    assertThat(-df).isEqualTo(AUGMENTED[FIFTH])

    val pf = PERFECT[FOURTH]
    assertThat(pf.semitones).isEqualTo(5)
    assertThat(-pf).isEqualTo(PERFECT[FIFTH])

    val af = AUGMENTED[FOURTH]
    assertThat(af.semitones).isEqualTo(6)
    assertThat(-af).isEqualTo(DIMINISHED[FIFTH])
  }

  @Test
  fun testFifth() {
    assertThrows<Exception> { MINOR[FIFTH] }
    assertThrows<Exception> { MAJOR[FIFTH] }

    val df = DIMINISHED[FIFTH]
    assertThat(df.semitones).isEqualTo(6)
    assertThat(-df).isEqualTo(AUGMENTED[FOURTH])

    val pf = PERFECT[FIFTH]
    assertThat(pf.semitones).isEqualTo(7)
    assertThat(-pf).isEqualTo(PERFECT[FOURTH])

    val af = AUGMENTED[FIFTH]
    assertThat(af.semitones).isEqualTo(8)
    assertThat(-af).isEqualTo(DIMINISHED[FOURTH])
  }
}
