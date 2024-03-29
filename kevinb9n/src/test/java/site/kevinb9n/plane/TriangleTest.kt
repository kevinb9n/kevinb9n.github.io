package site.kevinb9n.plane

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import site.kevinb9n.plane.UnTriangle.Companion.triangle
import site.kevinb9n.plane.Vector2D.Companion.vector

class TriangleTest {
  @Test
  fun testTriangle() {
    val a = Point2D(8, 6)
    val b = Point2D(7, 5)
    val c = Point2D(3, 9)

    val tri = triangle(a, b, c)
    assertThat(triangle(a, c, b)).isEqualTo(tri)
    assertThat(triangle(b, a, c)).isEqualTo(tri)
    assertThat(triangle(b, c, a)).isEqualTo(tri)
    assertThat(triangle(c, a, b)).isEqualTo(tri)
    assertThat(triangle(c, b, a)).isEqualTo(tri)

    val d = Point2D(5, 3)
    val tri2 = triangle(a, b, d)
    assertThat(triangle(a, d, b)).isEqualTo(tri2)
    assertThat(triangle(b, a, d)).isEqualTo(tri2)
    assertThat(triangle(b, d, a)).isEqualTo(tri2)
    assertThat(triangle(d, a, b)).isEqualTo(tri2)
    assertThat(triangle(d, b, a)).isEqualTo(tri2)

    val v1 = vector(-1, -1)
    val v2 = vector(-4, 4)
    val v3 = vector(5, -3)
    assertThat(triangle(v1, v2)).isEqualTo(tri)
    assertThat(triangle(v2, v3)).isEqualTo(tri)
    assertThat(triangle(v3, v1)).isEqualTo(tri)
    assertThat(triangle(-v2, -v1)).isEqualTo(tri)
    assertThat(triangle(-v3, -v2)).isEqualTo(tri)
    assertThat(triangle(-v1, -v3)).isEqualTo(tri)

    val e = Point2D(2, -4)
    val f = Point2D(3, -5)
    val g = Point2D(-1, -9)
    assertThat(triangle(e, f, g)).isEqualTo(tri)
    assertThat(triangle(e, g, f)).isEqualTo(tri)
    assertThat(triangle(f, e, g)).isEqualTo(tri)
    assertThat(triangle(f, g, e)).isEqualTo(tri)
    assertThat(triangle(g, e, f)).isEqualTo(tri)
    assertThat(triangle(g, f, e)).isEqualTo(tri)
  }

  @Test
  fun testCentroid() {
    val t = triangle(vector(12, 0), vector(0, 9))
    assertThat(t.leg1).isEqualTo(vector(12, 0))
    assertThat(t.leg2.x).isWithin(1e-14).of(0.0)
    assertThat(t.leg2.y).isWithin(1e-14).of(9.0)
    assertThat(t.leg3).isEqualTo(vector(-12, -9))
    assertThat(t.centroid()).isEqualTo(vector(8.0, 3.0))

    val t2 = triangle(Point2D(-45, -26), Point2D(45, -26), Point2D(0, 52))
    assertThat(t2.centroid()).isEqualTo(vector(45.04165047452724, 25.985567581458017))
  }

  @Test
  fun testFromSides() {
    val t = triangle(8.0, 6.0, 7.0)
    assertThat(t.leg1).isEqualTo(vector(6.0, 0.0))
    assertThat(t.leg2.magnitude).isWithin(1e-14).of(7.0)
    assertThat(t.leg3.magnitude).isWithin(1e-14).of(8.0)
  }
}
