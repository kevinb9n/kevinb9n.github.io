package site.kevinb9n.plane

import kotlin.math.PI

data class Circle(val center: Point2, val radius: Double) : PosClosedShape {
  init { require(radius >= 0.0) }
  override operator fun contains(point: Point2) = center.distance(point) <= radius + radius * 1e-14
  override fun area() = PI * radius * radius

  override fun similar(other: ClosedShape) = other is Circle
}
