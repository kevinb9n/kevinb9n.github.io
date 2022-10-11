package site.kevinb9n.plane

import site.kevinb9n.javafx.mean

fun enclose(a: Point) = Circle(a, 0.0)

fun enclose(a: Point, b: Point): Circle {
  val halfway = (b - a) / 2.0
  return Circle(a + halfway, halfway.magnitude)
}

fun enclose(a: Point, b: Point, c: Point): Circle {
  val bmc = b - c
  val amb = a - b
  val cma = c - a

  val o = Point(
    mean(minOf(a.x, b.x, c.x), maxOf(a.x, b.x, c.x)),
    mean(minOf(a.y, b.y, c.y), maxOf(a.y, b.y, c.y)))
  val ao = a - o
  val bo = b - o
  val co = c - o

  val d = 2 * (ao.x * bmc.y + bo.x * cma.y + co.x * amb.y)
  val x = (ao.magsq * bmc.y + bo.magsq * cma.y + co.magsq * amb.y) / d
  val y = (ao.magsq * bmc.x + bo.magsq * cma.x + co.magsq * amb.x) / -d
  with(o + CartesianVector(x, y)) {
    return Circle(this, maxOf(distance(a), distance(b), distance(c)))
  }
}

fun enclose(a: Point, b: Point, c: Point, d: Point, vararg rest: Point) =
  enclose(listOf(a, b, c, d) + rest.toList())

fun enclose(points: List<Point>): Circle {
  require(points.isNotEmpty())
  var circle = enclose(points[0])
  for (i in 1 until points.size) {
    val point = points[i]
    if (point !in circle) {
      circle = makeCircleOnePoint(point, points.subList(0, i))
    }
  }
  return circle
}

private fun makeCircleOnePoint(newPoint: Point, oldPoints: List<Point>): Circle {
  var circle = enclose(newPoint, oldPoints[0])
  for (i in 1 until oldPoints.size) {
    val nextPoint = oldPoints[i]
    if (!circle.contains(nextPoint)) {
      circle = makeCircleTwoPoints(newPoint, nextPoint, oldPoints.subList(0, i))
    }
  }
  return circle
}

private fun makeCircleTwoPoints(newPoint1: Point, newPoint2: Point, oldPoints: List<Point>): Circle {
  val circle = enclose(newPoint1, newPoint2)
  val vector: Vector = newPoint2 - newPoint1
  var left: Circle? = null
  var right: Circle? = null

  for (nextPoint in oldPoints.filter { it !in circle }) {
    val candidate = enclose(newPoint1, newPoint2, nextPoint)
    val cross = vector.crossMag(nextPoint - newPoint1)
    if (cross > 0 && (shouldReplace(vector, candidate, newPoint1, left))) {
      left = candidate
    }
    if (cross < 0 && (shouldReplace(vector, candidate, newPoint1, right))) {
      right = candidate
    }
  }

  return if (left == null) {
    right ?: circle
  } else if (right == null) {
    return left
  } else {
    listOf(left, right).minByOrNull { it.radius }!!
  }
}

private fun shouldReplace(vector: Vector, candidate: Circle, newPoint: Point, c: Circle?) =
  c == null || vector.crossMag(candidate.center - newPoint) > vector.crossMag(c.center - newPoint)

data class Circle(val center: Point, val radius: Double) {
  init { require(radius >= 0.0) }
  operator fun contains(p: Point) = center.distance(p) <= radius
}
