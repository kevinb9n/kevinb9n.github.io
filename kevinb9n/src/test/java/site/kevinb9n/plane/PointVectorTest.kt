package site.kevinb9n.plane

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.plane.Angle.Companion.degrees
import site.kevinb9n.plane.Vector2.Companion.times
import site.kevinb9n.plane.Vector2.Companion.vector
import kotlin.math.sqrt

class PointVectorTest {
  @Test
  fun testBasicOps() {
    assertThat(Point2.ORIGIN + CartesianVector2(3, 4)).isEqualTo(Point2(3, 4))
    assertThat(Point2.ORIGIN - Point2(3, 4)).isEqualTo(CartesianVector2(-3, -4))
    assertThat(Point2.ORIGIN - CartesianVector2(3, 4)).isEqualTo(Point2(-3, -4))
    assertThat(Point2(1, 2).distance(Point2(4, 6))).isEqualTo(5)
    assertThat(Vector2.ZERO + CartesianVector2(3, 4)).isEqualTo(CartesianVector2(3, 4))
    assertThat(Vector2.ZERO + Point2(3, 4)).isEqualTo(Point2(3, 4))
    assertThat(CartesianVector2(3, 4) - Vector2.ZERO).isEqualTo(CartesianVector2(3, 4))
    assertThat(-CartesianVector2(3, 4)).isEqualTo(CartesianVector2(-3, -4))
    assertThat(CartesianVector2(3, 4) * 2).isEqualTo(CartesianVector2(6, 8))
    assertThat(2 * CartesianVector2(3, 4)).isEqualTo(CartesianVector2(6, 8))
    assertThat(CartesianVector2(3, 4) / 2).isEqualTo(CartesianVector2(1.5, 2))
  }

  @Test fun testMagnitude() {
    assertThat(Vector2.ZERO.magnitude).isEqualTo(0.0)
    assertThat(CartesianVector2(3, 4).magnitude).isEqualTo(5.0)
    assertThat(CartesianVector2(3, -4).magnitude).isEqualTo(5.0)
    assertThat(CartesianVector2(-3, -4).magnitude).isEqualTo(5.0)
  }

  @Test fun testUnitVector() {
    assertThat(CartesianVector2(3, 4).unitVector()).isEqualTo(CartesianVector2(0.6, 0.8))
  }

  @Test fun testDirection() {
    assertThat(CartesianVector2(-1, 0).direction).isEqualTo(degrees(-180.0))
    assertThat(CartesianVector2(-1, -1).direction).isEqualTo(degrees(-135.0))
    assertThat(CartesianVector2(0, -1).direction).isEqualTo(degrees(-90.0))
    assertThat(CartesianVector2(1, -1).direction).isEqualTo(degrees(-45.0))
    assertThat(CartesianVector2(1, 0).direction).isEqualTo(degrees(0.0))
    assertThat(CartesianVector2(1, 1).direction).isEqualTo(degrees(45.0))
    assertThat(CartesianVector2(0, 1).direction).isEqualTo(degrees(90.0))
    assertThat(CartesianVector2(-1, 1).direction).isEqualTo(degrees(135.0))

    assertThat(CartesianVector2(1, 2 - sqrt(3.0)).direction).isEqualTo(degrees(15.0))
    assertThat(CartesianVector2(1, sqrt(2.0) - 1.0).direction).isEqualTo(degrees(22.5))
    assertThat(CartesianVector2(sqrt(3.0), 1.0).direction).isEqualTo(degrees(30.0))
  }

  @Test fun testDistance() {
    assertThat(Point2(1, 2).distance(Point2(1, 2))).isEqualTo(0.0)
    assertThat(Point2(1, 2).distance(Point2(4, 6))).isEqualTo(5.0)
  }

  @Test fun stuff() {
    assertThat(0.0.mod(5.0)).isEqualTo(0.0)
    assertThat(1.0.mod(5.0)).isEqualTo(1.0)
    assertThat(4.999.mod(5.0)).isEqualTo(4.999)
    assertThat(5.0.mod(5.0)).isEqualTo(0.0)
    assertThat((-4.0).mod(5.0)).isEqualTo(1.0)
  }

  @Test fun creation() {
    val rad3 = sqrt(3.0)
    val d60 = degrees(60.0)

    val cv = vector(x=1.0, y=rad3)
    assertThat(vector(x=1.0, y=rad3, magnitude=2.0, direction=d60)).isEqualTo(cv)
    assertThat(vector(x=1.0, y=rad3, magnitude=2.0)).isEqualTo(cv)
    assertThat(vector(x=1.0, y=rad3, direction=d60)).isEqualTo(cv)
    assertThat(vector(x=1.0, magnitude=2.0, direction=d60)).isEqualTo(cv)
    assertThat(vector(y=rad3, magnitude=2.0, direction=d60)).isEqualTo(cv)
    assertThat(vector(x=1.0, magnitude=2.0)).isEqualTo(cv)
    assertThat(vector(x=1.0, direction= d60)).isEqualTo(cv)
    assertThat(vector(y=rad3, magnitude=2.0)).isEqualTo(cv)
    assertThat(vector(y=rad3, direction= d60)).isEqualTo(cv)
    assertThat(vector(magnitude=2.0, direction= d60)).isEqualTo(cv)

    val pv = vector(magnitude = 2.0, direction = d60)
    assertThat(vector(x=1.0, y=rad3, magnitude=2.0, direction=d60)).isEqualTo(pv)
    assertThat(vector(x=1.0, y=rad3, magnitude=2.0)).isEqualTo(pv)
    assertThat(vector(x=1.0, y=rad3, direction= d60)).isEqualTo(pv)
    assertThat(vector(x=1.0, magnitude=2.0, direction= d60)).isEqualTo(pv)
    assertThat(vector(y=rad3, magnitude=2.0, direction= d60)).isEqualTo(pv)
    assertThat(vector(x=1.0, y=rad3)).isEqualTo(pv)
    assertThat(vector(x=1.0, magnitude=2.0)).isEqualTo(pv)
    assertThat(vector(x=1.0, direction= d60)).isEqualTo(pv)
    assertThat(vector(y=rad3, magnitude=2.0)).isEqualTo(pv)
    assertThat(vector(y=rad3, direction= d60)).isEqualTo(pv)
  }
}
