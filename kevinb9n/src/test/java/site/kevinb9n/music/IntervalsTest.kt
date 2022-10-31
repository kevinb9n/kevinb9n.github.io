package site.kevinb9n.music

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import site.kevinb9n.music.Accidental.FLAT
import site.kevinb9n.music.Accidental.NATURAL
import site.kevinb9n.music.Accidental.SHARP
import site.kevinb9n.music.PitchLetter.*
import site.kevinb9n.music.Quality.*
import site.kevinb9n.music.SimpleIntervalSize.*

class IntervalsTest {
  @Test fun all() {
    val ints = listOf(
      interval(PERFECT, UNISON), interval(AUGMENTED, UNISON),
      interval(DIMINISHED, SECOND), interval(MINOR, SECOND), interval(MAJOR, SECOND), interval(AUGMENTED, SECOND),
      interval(DIMINISHED, THIRD), interval(MINOR, THIRD), interval(MAJOR, THIRD), interval(AUGMENTED, THIRD),
      interval(DIMINISHED, FOURTH), interval(PERFECT, FOURTH), interval(AUGMENTED, FOURTH),
    )
    val theRest = ints.map { -it }.reversed()
    assertThat(allSimpleIntervals()).containsExactlyElementsIn(ints + theRest).inOrder()
  }

  @Test fun fuckup () {
    val c = PitchClassSpelling(C)
    val i3 = interval(MAJOR, THIRD)
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

  @Test fun testUnison() {
    assertThrows<Exception> { interval(DIMINISHED, UNISON) }
    assertThrows<Exception> { interval(MINOR, UNISON) }
    assertThrows<Exception> { interval(MAJOR, UNISON) }

    val pu = interval(PERFECT, UNISON)
    assertThat(pu.semitones).isEqualTo(0)
    assertThat(-pu).isEqualTo(interval(PERFECT, OCTAVE))

    val au = interval(AUGMENTED, UNISON)
    assertThat(au.semitones).isEqualTo(1)
    assertThat(-au).isEqualTo(interval(DIMINISHED, OCTAVE))
  }

  @Test fun testSecond() {
    assertThrows<Exception> { interval(PERFECT, SECOND) }

    val ds = interval(DIMINISHED, SECOND)
    assertThat(ds.semitones).isEqualTo(0)
    assertThat(-ds).isEqualTo(interval(AUGMENTED, SEVENTH))

    val mis = interval(MINOR, SECOND)
    assertThat(mis.semitones).isEqualTo(1)
    assertThat(-mis).isEqualTo(interval(MAJOR, SEVENTH))

    val mas = interval(MAJOR, SECOND)
    assertThat(mas.semitones).isEqualTo(2)
    assertThat(-mas).isEqualTo(interval(MINOR, SEVENTH))

    val aus = interval(AUGMENTED, SECOND)
    assertThat(aus.semitones).isEqualTo(3)
    assertThat(-aus).isEqualTo(interval(DIMINISHED, SEVENTH))
  }

  @Test fun testThird() {
    assertThrows<Exception> { interval(PERFECT, THIRD) }

    val dt = interval(DIMINISHED, THIRD)
    assertThat(dt.semitones).isEqualTo(2)
    assertThat(-dt).isEqualTo(interval(AUGMENTED, SIXTH))

    val mit = interval(MINOR, THIRD)
    assertThat(mit.semitones).isEqualTo(3)
    assertThat(-mit).isEqualTo(interval(MAJOR, SIXTH))

    val mat = interval(MAJOR, THIRD)
    assertThat(mat.semitones).isEqualTo(4)
    assertThat(-mat).isEqualTo(interval(MINOR, SIXTH))

    val at = interval(AUGMENTED, THIRD)
    assertThat(at.semitones).isEqualTo(5)
    assertThat(-at).isEqualTo(interval(DIMINISHED, SIXTH))
  }

  @Test fun testFourth() {
    assertThrows<Exception> { interval(MINOR, FOURTH) }
    assertThrows<Exception> { interval(MAJOR, FOURTH) }

    val df = interval(DIMINISHED, FOURTH)
    assertThat(df.semitones).isEqualTo(4)
    assertThat(-df).isEqualTo(interval(AUGMENTED, FIFTH))

    val pf = interval(PERFECT, FOURTH)
    assertThat(pf.semitones).isEqualTo(5)
    assertThat(-pf).isEqualTo(interval(PERFECT, FIFTH))

    val af = interval(AUGMENTED, FOURTH)
    assertThat(af.semitones).isEqualTo(6)
    assertThat(-af).isEqualTo(interval(DIMINISHED, FIFTH))
  }

  @Test fun testFifth() {
    assertThrows<Exception> { interval(MINOR, FIFTH) }
    assertThrows<Exception> { interval(MAJOR, FIFTH) }

    val df = interval(DIMINISHED, FIFTH)
    assertThat(df.semitones).isEqualTo(6)
    assertThat(-df).isEqualTo(interval(AUGMENTED, FOURTH))

    val pf = interval(PERFECT, FIFTH)
    assertThat(pf.semitones).isEqualTo(7)
    assertThat(-pf).isEqualTo(interval(PERFECT, FOURTH))

    val af = interval(AUGMENTED, FIFTH)
    assertThat(af.semitones).isEqualTo(8)
    assertThat(-af).isEqualTo(interval(DIMINISHED, FOURTH))
  }
}
