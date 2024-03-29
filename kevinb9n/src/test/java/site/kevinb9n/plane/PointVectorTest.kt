package site.kevinb9n.plane

import com.google.common.collect.Iterables
import com.google.common.math.Stats
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.javafx.random
import site.kevinb9n.math.mean
import site.kevinb9n.plane.Angle.Companion.degrees
import site.kevinb9n.plane.Vector2D.Companion.vector
import java.util.*
import kotlin.math.absoluteValue
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
    assertThat(CartesianVector2D(3, 4) * 2.0).isEqualTo(CartesianVector2D(6, 8))
    assertThat(2.0 * CartesianVector2D(3, 4)).isEqualTo(CartesianVector2D(6, 8))
    assertThat(CartesianVector2D(3, 4) / 2.0).isEqualTo(CartesianVector2D(1.5, 2))
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
    val toler = 1e-15

    val cv = vector(x=1.0, y=rad3)
    assertThat(vector(x=1.0, y=rad3, magnitude=2.0, direction=d60).distance(cv)).isLessThan(toler)
    assertThat(vector(x=1.0, y=rad3, magnitude=2.0).distance(cv)).isLessThan(toler)
    assertThat(vector(x=1.0, y=rad3, direction=d60).distance(cv)).isLessThan(toler)
    assertThat(vector(x=1.0, magnitude=2.0, direction=d60).distance(cv)).isLessThan(toler)
    assertThat(vector(y=rad3, magnitude=2.0, direction=d60).distance(cv)).isLessThan(toler)
    assertThat(vector(x=1.0, magnitude=2.0).distance(cv)).isLessThan(toler)
    assertThat(vector(x=1.0, direction=d60).distance(cv)).isLessThan(toler)
    assertThat(vector(y=rad3, magnitude=2.0).distance(cv)).isLessThan(toler)
    assertThat(vector(y=rad3, direction=d60).distance(cv)).isLessThan(toler)
    assertThat(vector(magnitude=2.0, direction=d60).distance(cv)).isLessThan(toler)

    val pv = vector(magnitude=2.0, direction=d60)
    assertThat(vector(x=1.0, y=rad3, magnitude=2.0, direction=d60).distance(pv)).isLessThan(toler)
    assertThat(vector(x=1.0, y=rad3, magnitude=2.0).distance(pv)).isLessThan(toler)
    assertThat(vector(x=1.0, y=rad3, direction=d60).distance(pv)).isLessThan(toler)
    assertThat(vector(x=1.0, magnitude=2.0, direction=d60).distance(pv)).isLessThan(toler)
    assertThat(vector(y=rad3, magnitude=2.0, direction=d60).distance(pv)).isLessThan(toler)
    assertThat(vector(x=1.0, y=rad3).distance(pv)).isLessThan(toler)
    assertThat(vector(x=1.0, magnitude=2.0).distance(pv)).isLessThan(toler)
    assertThat(vector(x=1.0, direction=d60).distance(pv)).isLessThan(toler)
    assertThat(vector(y=rad3, magnitude=2.0).distance(pv)).isLessThan(toler)
    assertThat(vector(y=rad3, direction=d60).distance(pv)).isLessThan(toler)
  }

  fun dot(a: Pair<Vector2D, Vector2D>, b: Pair<Vector2D, Vector2D>): Double {
    val x = doubleArrayOf(
      a.first.dot(b.first),
      a.first.dot(b.second),
      a.second.dot(b.first),
      a.second.dot(b.second),
    )
    val mean = Stats.meanOf(*x)
    for (y in x) {
      assertThat(y).isWithin(1e-14).of(mean)
    }
    return mean
  }

  fun cross(a: Pair<Vector2D, Vector2D>, b: Pair<Vector2D, Vector2D>): Double {
    val variations = doubleArrayOf(
      a.first crossZ b.first,
      a.first crossZ b.second,
      a.second crossZ b.first,
      a.second crossZ b.second,
    )
    val mean = Stats.meanOf(*variations)
    for (y in variations) {
      assertThat(y).isWithin(1e-14).of(mean)
    }
    return mean
  }

  @Test fun products() {
    val v1at30 = vector(magnitude=1.0, direction=degrees(30)) to vector(sqrt(3.0) / 2.0, 0.5)
    val v2at90 = vector(magnitude=2.0, direction=degrees(90)) to vector(0.0, 2.0)

    assertThat(dot(v1at30, v1at30)).isWithin(1e-14).of(1.0)
    assertThat(cross(v1at30, v1at30)).isWithin(1e-14).of(0.0)
    assertThat(dot(v2at90, v2at90)).isWithin(1e-14).of(4.0)
    assertThat(cross(v2at90, v2at90)).isWithin(1e-14).of(0.0)

    assertThat(dot(v1at30, v2at90)).isWithin(1e-14).of(1.0)
    assertThat(dot(v2at90, v1at30)).isWithin(1e-14).of(1.0)
    assertThat(cross(v1at30, v2at90)).isWithin(1e-14).of(sqrt(3.0))
    assertThat(cross(v2at90, v1at30)).isWithin(1e-14).of(-sqrt(3.0))
  }

  @Test fun drift() {
    var worst: Vector2D = vector(0.0, 0.0)
    var worstDrift: Double = Double.NEGATIVE_INFINITY
    var worstCycleLength: Int = 0
    var worstDelay: Int = 0
    var total = 0
    for (i in 1..10_000_000) {
      val set = LinkedHashSet<Vector2D>()
      val orig = vector(magnitude=random(100.0).absoluteValue, direction=degrees(random(180.0)))
      var latest = orig
      while (true) {
        if (!set.add(latest)) break
        latest = roundTrip(latest)
      }
      total += set.size
      val delay = Iterables.indexOf(set) { it == latest }
      val cycleLength = set.size - delay
      val cross = latest.crossZ(orig).absoluteValue / latest.magnitude / orig.magnitude
      worstDelay = maxOf(worstDelay, delay)
      worstCycleLength = maxOf(worstCycleLength, cycleLength)
      worstDrift = maxOf(worstDrift, cross)
    }
    println("$worstDelay $worstCycleLength $worstDrift ${total / 10_000_000.0}")
  }
}

fun roundTrip(v: Vector2D): Vector2D {
  val cartes = vector(v.x, v.y)
  val polar = vector(magnitude=cartes.magnitude, direction=cartes.direction)
  return vector(magnitude=mean(v.magnitude, polar.magnitude), direction=Angle.seconds(mean(v
    .direction.seconds, polar.direction.seconds)))
}
