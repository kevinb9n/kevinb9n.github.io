package site.kevinb9n.plane.stuff

import site.kevinb9n.plane.Point
import site.kevinb9n.plane.Circle
import site.kevinb9n.plane.Vector.Companion.vector
import site.kevinb9n.plane.sumProduct
import kotlin.math.max

/*
* Smallest enclosing circle - Library (Kotlin)
*
* Copyright (c) 2020 Project Nayuki
* https://www.nayuki.io/page/smallest-enclosing-circle
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program (see COPYING.txt and COPYING.LESSER.txt).
* If not, see <http://www.gnu.org/licenses/>.
*/

fun makeCircle(points: List<Point>): Circle {
  var c: Circle = makeCircleOnePoint(points.subList(0, 1), points[0])
  for (i in 1 until points.size) {
    val p = points[i]
    if (p !in c) {
      c = makeCircleOnePoint(points.subList(0, i), p)
    }
  }
  return c
}

// One boundary point known
private fun makeCircleOnePoint(points: List<Point>, p: Point): Circle {
  var c = makeDiameter(p, points[0])

  for (i in 1 until points.size) {
    val q = points[i]
    if (q !in c) {
      c = makeCircleTwoPoints(points.subList(0, i), p, q)
    }
  }
  return c
}

// Two boundary points known
private fun makeCircleTwoPoints(points: List<Point>, p: Point, q: Point): Circle {
  val pqCircle = makeDiameter(p, q)
  var left: Circle? = null
  var right: Circle? = null

  // For each point not in the two-point circle
  val vector = q - p
  for (r in points) {
    if (pqCircle.contains(r)) continue

    // Form a circumcircle and classify it on left or right side
    val crossMag = vector.crossMag(r - p)
    val circle = threePointCircle(p, q, r) ?: continue
    if (crossMag > 0 && (left == null || vector.crossMag(circle.center - p) > vector.crossMag(left.center - p))) {
      left = circle
    } else if (crossMag < 0 && (right == null || vector.crossMag(circle.center - p) < vector.crossMag(right.center - p))) {
      right = circle
    }
  }

  // Select which circle to return
  return if (left == null) {
    right ?: pqCircle
  } else if (right == null) {
    left
  } else {
    listOf(left, right).minByOrNull { it.radius }!!
  }
}

fun makeDiameter(a: Point, b: Point): Circle {
  val c = a.midpoint(b)
  return Circle(c, max(c.distance(a), c.distance(b)))
}

fun edges(points: List<Point>) = points.windowed(2).map { it[0] - it[1] }

fun threePointCircle(a: Point, b: Point, c: Point): Circle? {
  val points = listOf(a, b, c)
  val boxCenter = BoundingBox(points).midpoint
  val relToBoxCenter = points.map { it - boxCenter }
  val edges = edges(points + points).subList(1, points.size + 1)
  // just an obnoxious way to say listOf(b - c, c - a, a - b)

  val denom = 2.0 * sumProduct(relToBoxCenter.map { it.x }, edges.map { it.y })
  if (denom == 0.0) return null // collinear, I guess?

  val center = boxCenter + vector(
    sumProduct(relToBoxCenter.map { it.magsq }, edges.map { it.y }) / denom,
    sumProduct(relToBoxCenter.map { it.magsq }, edges.map { it.x }) / -denom,
  )
  val radius = listOf(a, b, c).maxOf { center.distance(it) }
  return Circle(center, radius)
}

data class BoundingBox private constructor(val min: Point, val max: Point) {
  constructor(points: List<Point>) : this(
    Point(points.minOf { it.x }, points.minOf { it.y }),
    Point(points.maxOf { it.x }, points.maxOf { it.y }))

  val xRange = min.x..max.x
  val yRange = min.y..max.y
  val midpoint = min.midpoint(max)
  operator fun contains(p: Point) = p.x in xRange && p.y in yRange
}
