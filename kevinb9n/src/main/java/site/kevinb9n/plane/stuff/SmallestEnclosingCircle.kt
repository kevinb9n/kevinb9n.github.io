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

package site.kevinb9n.plane.stuff

import site.kevinb9n.plane.BoundingBox
import site.kevinb9n.plane.Point
import site.kevinb9n.plane.Circle
import site.kevinb9n.plane.Vector.Companion.vector
import site.kevinb9n.plane.sumProduct
import kotlin.math.max

fun enclosingCircle(a: Point) = Circle(a, 0.0)

fun enclosingCircle(a: Point, b: Point): Circle {
  val c = a.midpoint(b)
  return Circle(c, max(c.distance(a), c.distance(b))) // damn fp error
}

fun enclosingCircle(a: Point, b: Point, c: Point): Circle { //?
  val points = listOf(a, b, c)
  val boxCenter = BoundingBox(points).midpoint
  val relToBoxCenter = points.map { it - boxCenter }
  val edges = listOf(b - c, c - a, a - b)

  val denom = 2.0 * sumProduct(relToBoxCenter.map { it.x }, edges.map { it.y })
  //if (denom == 0.0) return null // collinear, I guess?

  val center = boxCenter + vector(
    sumProduct(relToBoxCenter.map { it.magsq }, edges.map { it.y }) / denom,
    sumProduct(relToBoxCenter.map { it.magsq }, edges.map { it.x }) / -denom,
  )
  val radius = listOf(a, b, c).maxOf { center.distance(it) }
  return Circle(center, radius)
}

fun enclosingCircle(points: List<Point>): Circle {
  var circle = enclosingCircle(points[0])
  for ((i, newPoint) in points.withIndex()) {
    if (newPoint !in circle) {
      circle = oneKnownBoundaryPoint(points.subList(0, i), newPoint)
    }
  }
  return circle
}

// One boundary point known
private fun oneKnownBoundaryPoint(points: List<Point>, bound: Point): Circle {
  var circle = enclosingCircle(bound, points[0])
  for ((i, newPoint) in points.withIndex()) {
    if (newPoint !in circle) {
      circle = twoKnownBoundaryPoints(points.subList(0, i), bound, newPoint)
    }
  }
  return circle
}

// Two boundary points known
private fun twoKnownBoundaryPoints(points: List<Point>, p: Point, q: Point): Circle {
  val pqCircle = enclosingCircle(p, q)
  var left: Circle? = null
  var right: Circle? = null

  // For each point not in the two-point circle
  val vector = q - p
  for (r in points) {
    if (pqCircle.contains(r)) continue

    // Form a circumcircle and classify it on left or right side
    val crossMag = vector.crossMag(r - p)
    val circle = enclosingCircle(p, q, r) ?: continue
    if (crossMag > 0 && (left == null || vector.crossMag(circle.center - p) > vector.crossMag(left.center - p))) {
      left = circle
    } else if (crossMag < 0 && (right == null || vector.crossMag(circle.center - p) < vector.crossMag(right.center - p))) {
      right = circle
    }
  }

  return when {
    left == null -> right ?: pqCircle
    right == null -> left
    else -> listOf(left, right).minByOrNull { it.radius }!!
  }
}
