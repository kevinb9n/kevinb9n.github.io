package site.kevinb9n.plane

// Goal is for == to be a congruency test
// Allowed to have zero area
interface ClosedShape: Object2D {
  fun area(): Number
}
