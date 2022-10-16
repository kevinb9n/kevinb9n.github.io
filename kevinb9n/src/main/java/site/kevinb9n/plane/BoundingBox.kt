package site.kevinb9n.plane

data class BoundingBox private constructor(val minCorner: Point, val maxCorner: Point) {
  constructor(points: List<Point>) : this(
          Point(points.minOf { it.x }, points.minOf { it.y }),
          Point(points.maxOf { it.x }, points.maxOf { it.y }))

  val xRange = minCorner.x..maxCorner.x
  val yRange = minCorner.y..maxCorner.y
  val midpoint = minCorner.midpoint(maxCorner)

  operator fun contains(p: Point) = p.x in xRange && p.y in yRange
}
