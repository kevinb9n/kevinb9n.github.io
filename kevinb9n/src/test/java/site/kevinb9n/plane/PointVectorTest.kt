package site.kevinb9n.plane

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.plane.Vector.Companion.times
import kotlin.math.sqrt

class PointVectorTest {
  @Test
  fun testBasicOps() {
    assertThat(Point.ORIGIN + Vector(3, 4)).isEqualTo(Point(3, 4))
    assertThat(Point.ORIGIN - Point(3, 4)).isEqualTo(Vector(-3, -4))
    assertThat(Point.ORIGIN - Vector(3, 4)).isEqualTo(Point(-3, -4))
    assertThat(Point(1, 2).distance(Point(4, 6))).isEqualTo(5)
    assertThat(Vector.ZERO + Vector(3, 4)).isEqualTo(Vector(3, 4))
    assertThat(Vector.ZERO + Point(3, 4)).isEqualTo(Point(3, 4))
    assertThat(Vector(3, 4) - Vector.ZERO).isEqualTo(Vector(3, 4))
    assertThat(-Vector(3, 4)).isEqualTo(Vector(-3, -4))
    assertThat(Vector(3, 4) * 2).isEqualTo(Vector(6, 8))
    assertThat(2 * Vector(3, 4)).isEqualTo(Vector(6, 8))
    assertThat(Vector(3, 4) / 2).isEqualTo(Vector(1.5, 2))
  }

  @Test fun testMagnitude() {
    assertThat(Vector.ZERO.magnitude()).isEqualTo(0.0)
    assertThat(Vector(3, 4).magnitude()).isEqualTo(5.0)
    assertThat(Vector(3, -4).magnitude()).isEqualTo(5.0)
    assertThat(Vector(-3, -4).magnitude()).isEqualTo(5.0)
  }

  @Test fun testUnitVector() {
    assertThat(Vector(3, 4).unitVector()).isEqualTo(Vector(0.6, 0.8))
  }

  @Test fun testDirection() {
    assertThat(Vector(-1, 0).direction().degrees).isEqualTo(-180.0)
    assertThat(Vector(-1, -1).direction().degrees).isEqualTo(-135.0)
    assertThat(Vector(0, -1).direction().degrees).isEqualTo(-90.0)
    assertThat(Vector(1, -1).direction().degrees).isEqualTo(-45.0)
    assertThat(Vector(1, 0).direction().degrees).isEqualTo(0.0)
    assertThat(Vector(1, 1).direction().degrees).isEqualTo(45.0)
    assertThat(Vector(0, 1).direction().degrees).isEqualTo(90.0)
    assertThat(Vector(-1, 1).direction().degrees).isEqualTo(135.0)

    assertThat(Vector(1, 2 - sqrt(3.0)).direction().degrees).isEqualTo(15.0)
    assertThat(Vector(1, sqrt(2.0) - 1.0).direction().degrees).isEqualTo(22.5)
    assertThat(Vector(sqrt(3.0), 1.0).direction().degrees).isEqualTo(30.0)
  }

  @Test fun testDistance() {
    assertThat(Point(1, 2).distance(Point(1, 2))).isEqualTo(0.0)
    assertThat(Point(1, 2).distance(Point(4, 6))).isEqualTo(5.0)
  }
}
