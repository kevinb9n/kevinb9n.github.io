package site.kevinb9n.plane

data class BoundingBox private constructor(val minCorner: Point2D, val maxCorner: Point2D) {
  constructor(points: List<Point2D>) : this(
          Point2D(points.minOf { it.x }, points.minOf { it.y }),
          Point2D(points.maxOf { it.x }, points.maxOf { it.y }))

  val xRange = minCorner.x..maxCorner.x
  val yRange = minCorner.y..maxCorner.y
  val midpoint = minCorner.midpoint(maxCorner)

  operator fun contains(p: Point2D) = p.x in xRange && p.y in yRange
}
