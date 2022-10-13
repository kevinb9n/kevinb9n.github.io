package site.kevinb9n.math

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

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
}
