package site.kevinb9n.math

import com.google.common.truth.Truth.assertThat
import javafx.animation.Interpolator
import org.junit.jupiter.api.Test
import site.kevinb9n.plane.Point

class MonotoneCubicInterpolatorTest {
  @Test
  fun testSimple() {
    val interp = interpolator(0.5, 0.5)
    assertThat(interp.interpolate(0.1)).isEqualTo(0.1)
    assertThat(interp.interpolate(0.3)).isEqualTo(0.3)
    assertThat(interp.interpolate(0.8)).isEqualTo(0.8)
  }

  @Test
  fun testSquared() {
    val interp = interpolator(0.31385933837, 0.63745860882)
    assertThat(interp.interpolate(0.45742710775)).isWithin(1e-10).of(0.20923955891)
    assertThat(interp.interpolate(0.75830573921)).isWithin(1e-10).of(0.57502759412)

//    val interp = interpolator(0.31385933836549283503, 0.6374586088176874243050)
//    assertThat(interp.interpolate(0.45742710775633810998)).isEqualTo(0.20923955891032855669)
//    assertThat(interp.interpolate(0.75830573921179161621)).isEqualTo(0.57502759412154171713)
  }

  @Test
  fun testInterp() {
    assertThat(Interpolator.LINEAR.interpolate(0.0, 100.0, 0.5)).isEqualTo(50.0)

    val linear2 = Interpolator.SPLINE(0.3, 0.3, 0.7, 0.7)
    assertThat(linear2.interpolate(0.0, 100.0, 0.5)).isEqualTo(50.0)
    assertThat(linear2.interpolate(0.0, 100.0, 0.1)).isEqualTo(10.0)

    val x2ish = Interpolator.SPLINE(0.25, 0.0625, 0.75, 0.5625)
    assertThat(x2ish.interpolate(0.0, 1.0, 0.25)).isEqualTo(0.0625)
    assertThat(x2ish.interpolate(0.0, 1.0, 0.75)).isEqualTo(0.5625)
  }

//  fun testXInterp() {
//    val ti = TransformingInterpolator(
//      setOf(Point(0.0, 0.0), Point(0.25, 0.0625), Point(0.75, 0.5625), Point(1.0, 1.0)),
//      Interpolator.LINEAR)
//
//  }
}
