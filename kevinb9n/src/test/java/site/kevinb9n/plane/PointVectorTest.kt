package site.kevinb9n.plane

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.plane.Vector.Companion.times
import kotlin.math.sqrt

class PointVectorTest {
  @Test
  fun testBasicOps() {
    assertThat(Point.ORIGIN + CartesianVector(3, 4)).isEqualTo(Point(3, 4))
    assertThat(Point.ORIGIN - Point(3, 4)).isEqualTo(CartesianVector(-3, -4))
    assertThat(Point.ORIGIN - CartesianVector(3, 4)).isEqualTo(Point(-3, -4))
    assertThat(Point(1, 2).distance(Point(4, 6))).isEqualTo(5)
    assertThat(Vector.ZERO + CartesianVector(3, 4)).isEqualTo(CartesianVector(3, 4))
    assertThat(Vector.ZERO + Point(3, 4)).isEqualTo(Point(3, 4))
    assertThat(CartesianVector(3, 4) - Vector.ZERO).isEqualTo(CartesianVector(3, 4))
    assertThat(-CartesianVector(3, 4)).isEqualTo(CartesianVector(-3, -4))
    assertThat(CartesianVector(3, 4) * 2).isEqualTo(CartesianVector(6, 8))
    assertThat(2 * CartesianVector(3, 4)).isEqualTo(CartesianVector(6, 8))
    assertThat(CartesianVector(3, 4) / 2).isEqualTo(CartesianVector(1.5, 2))
  }

  @Test fun testMagnitude() {
    assertThat(Vector.ZERO.magnitude).isEqualTo(0.0)
    assertThat(CartesianVector(3, 4).magnitude).isEqualTo(5.0)
    assertThat(CartesianVector(3, -4).magnitude).isEqualTo(5.0)
    assertThat(CartesianVector(-3, -4).magnitude).isEqualTo(5.0)
  }

  @Test fun testUnitVector() {
    assertThat(CartesianVector(3, 4).unitVector()).isEqualTo(CartesianVector(0.6, 0.8))
  }

  @Test fun testDirection() {
    assertThat(CartesianVector(-1, 0).direction).isEqualTo(Angle.fromDegrees(-180.0))
    assertThat(CartesianVector(-1, -1).direction).isEqualTo(Angle.fromDegrees(-135.0))
    assertThat(CartesianVector(0, -1).direction).isEqualTo(Angle.fromDegrees(-90.0))
    assertThat(CartesianVector(1, -1).direction).isEqualTo(Angle.fromDegrees(-45.0))
    assertThat(CartesianVector(1, 0).direction).isEqualTo(Angle.fromDegrees(0.0))
    assertThat(CartesianVector(1, 1).direction).isEqualTo(Angle.fromDegrees(45.0))
    assertThat(CartesianVector(0, 1).direction).isEqualTo(Angle.fromDegrees(90.0))
    assertThat(CartesianVector(-1, 1).direction).isEqualTo(Angle.fromDegrees(135.0))

    assertThat(CartesianVector(1, 2 - sqrt(3.0)).direction).isEqualTo(Angle.fromDegrees(15.0))
    assertThat(CartesianVector(1, sqrt(2.0) - 1.0).direction).isEqualTo(Angle.fromDegrees(22.5))
    assertThat(CartesianVector(sqrt(3.0), 1.0).direction).isEqualTo(Angle.fromDegrees(30.0))
  }

  @Test fun testDistance() {
    assertThat(Point(1, 2).distance(Point(1, 2))).isEqualTo(0.0)
    assertThat(Point(1, 2).distance(Point(4, 6))).isEqualTo(5.0)
  }

  @Test fun stuff() {
    assertThat(0.0.mod(5.0)).isEqualTo(0.0)
    assertThat(1.0.mod(5.0)).isEqualTo(1.0)
    assertThat(4.999.mod(5.0)).isEqualTo(4.999)
    assertThat(5.0.mod(5.0)).isEqualTo(0.0)
    assertThat((-4.0).mod(5.0)).isEqualTo(1.0)
  }
}
