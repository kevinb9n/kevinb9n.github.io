package site.kevinb9n.plane

import kotlin.math.PI

data class Circle(val center: Point, val radius: Double) : PosClosedShape {
  init { require(radius >= 0.0) }
  override operator fun contains(point: Point) = center.distance(point) <= radius + radius * 1e-14
  override fun area() = PI * radius * radius

  override fun similar(other: ClosedShape) = other is Circle
}
