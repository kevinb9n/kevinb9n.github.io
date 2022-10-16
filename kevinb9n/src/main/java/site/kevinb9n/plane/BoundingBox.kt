package site.kevinb9n.plane

data class BoundingBox private constructor(val minCorner: Point2, val maxCorner: Point2) {
  constructor(points: List<Point2>) : this(
          Point2(points.minOf { it.x }, points.minOf { it.y }),
          Point2(points.maxOf { it.x }, points.maxOf { it.y }))

  val xRange = minCorner.x..maxCorner.x
  val yRange = minCorner.y..maxCorner.y
  val midpoint = minCorner.midpoint(maxCorner)

  operator fun contains(p: Point2) = p.x in xRange && p.y in yRange
}
