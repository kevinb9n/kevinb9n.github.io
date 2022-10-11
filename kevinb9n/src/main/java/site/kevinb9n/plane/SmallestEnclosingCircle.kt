package site.kevinb9n.plane

import site.kevinb9n.javafx.mean

fun enclose(a: Point) = Circle(a, 0.0)

fun enclose(a: Point, b: Point): Circle {
  val halfway = (b - a) / 2.0
  return Circle(a + halfway, halfway.magnitude)
}

fun threePointCircle(a: Point, b: Point, c: Point): Circle? {
  val bmc = b - c
  val amb = a - b
  val cma = c - a

  val o = Point(
    mean(minOf(a.x, b.x, c.x), maxOf(a.x, b.x, c.x)),
    mean(minOf(a.y, b.y, c.y), maxOf(a.y, b.y, c.y)))
  val amo = a - o
  val bmo = b - o
  val cmo = c - o
  // +- -+ +-
  val d = 2 * (amo.x * bmc.y + bmo.x * cma.y + cmo.x * amb.y)
  if (d == 0.0) return null
  val x = (amo.magsq * bmc.y + bmo.magsq * cma.y + cmo.magsq * amb.y) / d
  val y = (amo.magsq * bmc.x + bmo.magsq * cma.x + cmo.magsq * amb.x) / -d
  with(o + CartesianVector(x, y)) {
    return Circle(this, maxOf(distance(a), distance(b), distance(c)))
  }
}

fun enclose(a: Point, b: Point, c: Point, vararg rest: Point) =
  enclose(listOf(a, b, c) + rest.toList())

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
    val candidate = threePointCircle(newPoint1, newPoint2, nextPoint) ?: continue
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
