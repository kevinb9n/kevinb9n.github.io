package site.kevinb9n.plane

import site.kevinb9n.math.sumProduct
import kotlin.math.PI
import kotlin.math.max

data class Circle(val center: Point2D, val radius: Double) : PosClosedShape {
  init { require(radius >= 0.0) }
  override operator fun contains(point: Point2D) = center.distance(point) <= radius + radius * 1e-14
  override fun area() = PI * radius * radius

  override fun similar(other: ClosedShape) = other is Circle

  companion object {
    fun enclosingCircle(a: Point2D) = Circle(a, 0.0)

    fun enclosingCircle(a: Point2D, b: Point2D): Circle {
      val c = a.midpoint(b)
      return Circle(c, max(c.distance(a), c.distance(b))) // damn fp error
    }

    fun enclosingCircle(a: Point2D, b: Point2D, c: Point2D): Circle { //?
      val points = listOf(a, b, c)
      val boxCenter = BoundingBox(points).midpoint
      val relToBoxCenter = points.map { it - boxCenter }
      val edges = listOf(b - c, c - a, a - b)

      val denom = 2.0 * sumProduct(relToBoxCenter.map { it.x }, edges.map { it.y })
      //if (denom == 0.0) return null // collinear, I guess?

      val center = boxCenter + Vector2D.vector(
        sumProduct(relToBoxCenter.map { it.magsq }, edges.map { it.y }) / denom,
        sumProduct(relToBoxCenter.map { it.magsq }, edges.map { it.x }) / -denom,
      )
      val radius = listOf(a, b, c).maxOf { center.distance(it) }
      return Circle(center, radius)
    }

    fun enclosingCircle(points: List<Point2D>): Circle {
      var circle = enclosingCircle(points[0])
      for ((i, newPoint) in points.withIndex()) {
        if (newPoint !in circle) {
          circle = oneKnownBoundaryPoint(points.subList(0, i), newPoint)
        }
      }
      return circle
    }

    // One boundary point known
    private fun oneKnownBoundaryPoint(points: List<Point2D>, bound: Point2D): Circle {
      var circle = enclosingCircle(bound, points[0])
      for ((i, newPoint) in points.withIndex()) {
        if (newPoint !in circle) {
          circle = twoKnownBoundaryPoints(points.subList(0, i), bound, newPoint)
        }
      }
      return circle
    }

    // Two boundary points known
    private fun twoKnownBoundaryPoints(points: List<Point2D>, p: Point2D, q: Point2D): Circle {
      val pqCircle = enclosingCircle(p, q)
      var left: Circle? = null
      var right: Circle? = null

      val vector = q - p
      for (r in points) {
        if (pqCircle.contains(r)) continue
        val crossMag = vector.cross(r - p)
        val circle = enclosingCircle(p, q, r)
        if (crossMag > 0 && (left == null || vector.cross(circle.center - p) > vector.cross(left.center - p))) {
          left = circle
        } else if (crossMag < 0 && (right == null || vector.cross(circle.center - p) < vector.cross(right.center - p))) {
          right = circle
        }
      }

      return when {
        left == null -> right ?: pqCircle
        right == null -> left
        else -> if (left.radius < right.radius) left else right
      }
    }
  }
}
