package site.kevinb9n.plane

import kotlin.math.PI

data class Circle(val radius: Double) : ClosedShape {
  init { require(radius >= 0.0) }
  override fun area() = PI * radius * radius
  override fun similar(other: ClosedShape) = other is Circle
}
