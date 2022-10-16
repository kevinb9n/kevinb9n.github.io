package site.kevinb9n.plane

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.plane.Angle.Companion.degrees
import site.kevinb9n.plane.Vector2D.Companion.vector
import kotlin.math.sqrt

class PointVectorTest {
  @Test
  fun testBasicOps() {
    assertThat(Point2D.ORIGIN + CartesianVector2D(3, 4)).isEqualTo(Point2D(3, 4))
    assertThat(Point2D.ORIGIN - Point2D(3, 4)).isEqualTo(CartesianVector2D(-3, -4))
    assertThat(Point2D.ORIGIN - CartesianVector2D(3, 4)).isEqualTo(Point2D(-3, -4))
    assertThat(Point2D(1, 2).distance(Point2D(4, 6))).isEqualTo(5)
    assertThat(Vector2D.ZERO + CartesianVector2D(3, 4)).isEqualTo(CartesianVector2D(3, 4))
    assertThat(Vector2D.ZERO + Point2D(3, 4)).isEqualTo(Point2D(3, 4))
    assertThat(CartesianVector2D(3, 4) - Vector2D.ZERO).isEqualTo(CartesianVector2D(3, 4))
    assertThat(-CartesianVector2D(3, 4)).isEqualTo(CartesianVector2D(-3, -4))
    assertThat(CartesianVector2D(3, 4) * 2).isEqualTo(CartesianVector2D(6, 8))
    assertThat(2 * CartesianVector2D(3, 4)).isEqualTo(CartesianVector2D(6, 8))
    assertThat(CartesianVector2D(3, 4) / 2).isEqualTo(CartesianVector2D(1.5, 2))
  }

  @Test fun testMagnitude() {
    assertThat(Vector2D.ZERO.magnitude).isEqualTo(0.0)
    assertThat(CartesianVector2D(3, 4).magnitude).isEqualTo(5.0)
    assertThat(CartesianVector2D(3, -4).magnitude).isEqualTo(5.0)
    assertThat(CartesianVector2D(-3, -4).magnitude).isEqualTo(5.0)
  }

  @Test fun testDirection() {
    assertThat(CartesianVector2D(-1, 0).direction).isEqualTo(degrees(-180.0))
    assertThat(CartesianVector2D(-1, -1).direction).isEqualTo(degrees(-135.0))
    assertThat(CartesianVector2D(0, -1).direction).isEqualTo(degrees(-90.0))
    assertThat(CartesianVector2D(1, -1).direction).isEqualTo(degrees(-45.0))
    assertThat(CartesianVector2D(1, 0).direction).isEqualTo(degrees(0.0))
    assertThat(CartesianVector2D(1, 1).direction).isEqualTo(degrees(45.0))
    assertThat(CartesianVector2D(0, 1).direction).isEqualTo(degrees(90.0))
    assertThat(CartesianVector2D(-1, 1).direction).isEqualTo(degrees(135.0))

    assertThat(CartesianVector2D(1, 2 - sqrt(3.0)).direction).isEqualTo(degrees(15.0))
    assertThat(CartesianVector2D(1, sqrt(2.0) - 1.0).direction).isEqualTo(degrees(22.5))
    assertThat(CartesianVector2D(sqrt(3.0), 1.0).direction).isEqualTo(degrees(30.0))
  }

  @Test fun testDistance() {
    assertThat(Point2D(1, 2).distance(Point2D(1, 2))).isEqualTo(0.0)
    assertThat(Point2D(1, 2).distance(Point2D(4, 6))).isEqualTo(5.0)
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
