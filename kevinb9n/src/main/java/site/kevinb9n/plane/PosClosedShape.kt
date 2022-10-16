package site.kevinb9n.plane

interface PosClosedShape : Positioned, ClosedShape {
  operator fun contains(point: Point2): Boolean
}
