package site.kevinb9n.plane

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.plane.Triangle.Companion.triangle

class TriangleTest {
  @Test
  fun testTriangle() {
    val a = Point(8, 6)
    val b = Point(7, 5)
    val c = Point(3, 9)

    val tri = triangle(a, b, c)
    assertThat(triangle(a, c, b)).isEqualTo(tri)
    assertThat(triangle(b, a, c)).isEqualTo(tri)
    assertThat(triangle(b, c, a)).isEqualTo(tri)
    assertThat(triangle(c, a, b)).isEqualTo(tri)
    assertThat(triangle(c, b, a)).isEqualTo(tri)

    val d = Point(5, 3)
    val tri2 = triangle(a, b, d)
    assertThat(triangle(a, d, b)).isEqualTo(tri2)
    assertThat(triangle(b, a, d)).isEqualTo(tri2)
    assertThat(triangle(b, d, a)).isEqualTo(tri2)
    assertThat(triangle(d, a, b)).isEqualTo(tri2)
    assertThat(triangle(d, b, a)).isEqualTo(tri2)

    val v1 = CartesianVector(-1, -1)
    val v2 = CartesianVector(-4, 4)
    val v3 = CartesianVector(5, -3)
    assertThat(triangle(v1, v2)).isEqualTo(tri)
    assertThat(triangle(v2, v3)).isEqualTo(tri)
    assertThat(triangle(v3, v1)).isEqualTo(tri)
    assertThat(triangle(-v2, -v1)).isEqualTo(tri)
    assertThat(triangle(-v3, -v2)).isEqualTo(tri)
    assertThat(triangle(-v1, -v3)).isEqualTo(tri)

    val e = Point(2, -4)
    val f = Point(3, -5)
    val g = Point(-1, -9)
    assertThat(triangle(e, f, g)).isEqualTo(tri)
    assertThat(triangle(e, g, f)).isEqualTo(tri)
    assertThat(triangle(f, e, g)).isEqualTo(tri)
    assertThat(triangle(f, g, e)).isEqualTo(tri)
    assertThat(triangle(g, e, f)).isEqualTo(tri)
    assertThat(triangle(g, f, e)).isEqualTo(tri)
  }

  @Test
  fun testCentroid() {
    val t = triangle(CartesianVector(12, 0), CartesianVector(0, 9))
    assertThat(t.leg1).isEqualTo(CartesianVector(12, 0))
    assertThat(t.leg2).isEqualTo(CartesianVector(0, 9))
    assertThat(t.leg3).isEqualTo(CartesianVector(-12, -9))
    assertThat(t.centroid()).isEqualTo(CartesianVector(8.0, 3.0))

    val t2 = triangle(Point(-45, -26), Point(45, -26), Point(0, 52))
    assertThat(t2.centroid()).isEqualTo(CartesianVector(45.041650474527216, 25.985567581458014))
  }

  @Test
  fun testCC() {
    assertThat(enclose(Point(8, 6), Point(7, 5), Point(3, 9))).isEqualTo(
      Circle(Point(5.5, 7.5), 2.9154759474226504))
  }
}