package site.kevinb9n.plane

import com.google.common.collect.Collections2
import com.google.common.collect.HashMultiset
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class EnclosingCircleTest {
  @Test
  fun test() {
    assertThat(enclose(listOf(Point.ORIGIN))).isEqualTo(Circle(Point.ORIGIN, 0.0))
    assertThat(enclose(listOf(Point(2, 3), Point(10, 9)))).isEqualTo(Circle(Point(6, 6), 5.0))
    assertThat(enclose(listOf(
      Point(5, 0), Point(3, 4), Point(4, 3), Point(0, 5), Point(-3, 4), Point(-4, 3), Point(-5, 0))))
      .isEqualTo(Circle(Point.ORIGIN, 5.0))


  }

  @Test
  fun dafuq() {
    val pts = listOf(
      Point(x=554.1518237937553, y=584.9494469567428),
      Point(x=204.94628334631685, y=286.8169294888452),
      Point(x=307.7983925624817, y=566.3972041378483),
      Point(x=436.0277954210054, y=222.59276893818472))

    val ct = Collections2.permutations(pts).forEach {
      val circ = enclose(it)
      assertThat(circ.center.x).isWithin(1e-12).of(379.5490535700361)
      assertThat(circ.center.y).isWithin(1e-12).of(435.883188222794)
      assertThat(circ.radius).isWithin(1e-12).of(229.57978321867427)
    }
  }

  @Test
  fun differentAndGood() {
    val pts = listOf(
      Point(x=554.152, y=584.950),
      Point(x=204.946, y=286.816),
      Point(x=307.798, y=566.397),
      Point(x=436.028, y=222.592))

    assertThat(enclose(pts)).isEqualTo(Circle(Point(x=379.549, y=435.88300000000004), 229.58043927564913))
  }

  @Test
  fun twoBug() {
    val pt1 = Point(567.9002227167875, 420.17797057277977)
    val pt2 = Point(484.64316482608444, 459.19045934543453)
    assertThat(enclose(listOf(pt1, pt2))).isEqualTo(Circle(pt1.midpoint(pt2), (pt2 - pt1).magnitude / 2.0))
  }

  @Test
  fun phuctup() {
    val pts = listOf(
      Point(207.1170409579347, 754.8549269718503),
      Point(239.13423064571052, 779.5550175241485),
      Point(313.82006004850894, 319.14001361931685),
      Point(464.1112454134134, 793.6092370600056),
      Point(754.2597253338755, 392.4303535825119),
      Point(754.4159916756881, 755.3336738384503),
    )

    val multiset = HashMultiset.create<Double>()
    multiset += Collections2.permutations(pts).map {
      enclose(it).radius
      //== Circle(Point(480.9249278223155, 573.9997454481017), 328.1453269408627)
    }
    println(multiset)
    assertThat(multiset.elementSet().size).isEqualTo(1) // lol
  }
}
