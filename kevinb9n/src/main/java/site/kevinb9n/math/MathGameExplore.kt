import com.google.common.collect.Collections2
import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableList
import com.google.common.collect.SetMultimap
import site.kevinb9n.math.roundToBinaryDecimalPlaces
import java.util.*

fun main() {
  val multimap: SetMultimap<Double, String> = HashMultimap.create()
  val all: List<Num> = ImmutableList.copyOf(EnumSet.allOf(Num::class.java))
  for (inputs in Collections2.permutations(all)) {
    for (c in Combiner.values()) {
      for (op1 in Op.values()) {
        for (op2 in Op.values()) {
          for (op3 in Op.values()) {
            val result = roundToBinaryDecimalPlaces(
              c.combine(inputs[0], inputs[1], inputs[2], inputs[3], op1, op2, op3), 30)
            val desc = String.format(c.fmt, inputs[0], op1, inputs[1], op2, inputs[2], op3, inputs[3])
            multimap.put(result, desc)
          }
        }
      }
    }
  }
  println(multimap.keySet().size)
  val smallestGap = doubleArrayOf(Double.POSITIVE_INFINITY)
  val last = doubleArrayOf(-Double.MAX_VALUE)
  val closest = doubleArrayOf(0.0)
  multimap.keySet().stream().sorted().forEach { d: Double ->
    println(d)
    for (s in multimap[d]) {
      println(s)
    }
    println()
    val gap = d - last[0]
    if (gap < smallestGap[0]) {
      smallestGap[0] = gap
      closest[0] = d
    }
    last[0] = d
  }
  println(smallestGap[0])
  println(closest[0])
}

private enum class Num(val value: Double) {
  a(1 + Math.random()),
  b(2 + Math.random()),
  c(3 + Math.random()),
  d(4 + Math.random())
}

private enum class Combiner(val fmt: String) {
  TREE("(%s %s %s) %s (%s %s %s)") {
    override fun combine(a: Num, b: Num, c: Num, d: Num, one: Op, two: Op, three: Op): Double {
      return two.op(one.op(a.value, b.value), three.op(c.value, d.value))
    }
  },
  POST_POST("((%s %s %s) %s %s) %s %s)") {
    override fun combine(a: Num, b: Num, c: Num, d: Num, one: Op, two: Op, three: Op): Double {
      return three.op(two.op(one.op(a.value, b.value), c.value), d.value)
    }
  },
  POST_PRE("%s %s ((%s %s %s) %s %s)") {
    override fun combine(a: Num, b: Num, c: Num, d: Num, one: Op, two: Op, three: Op): Double {
      return one.op(a.value, three.op(two.op(b.value, c.value), d.value))
    }
  },
  PRE_POST("(%s %s (%s %s %s)) %s %s") {
    override fun combine(a: Num, b: Num, c: Num, d: Num, one: Op, two: Op, three: Op): Double {
      return three.op(one.op(a.value, two.op(b.value, c.value)), d.value)
    }
  },
  PRE_PRE("%s %s (%s %s (%s %s %s))") {
    override fun combine(a: Num, b: Num, c: Num, d: Num, one: Op, two: Op, three: Op): Double {
      return one.op(a.value, two.op(b.value, three.op(c.value, d.value)))
    }
  },
  ;

  abstract fun combine(a: Num, b: Num, c: Num, d: Num, one: Op, two: Op, three: Op): Double
}

enum class Op(var s: String, var commutes: Boolean) {
  PLUS("+", true) { override fun op(a: Double, b: Double) = a + b },
  MINUS("-", false) { override fun op(a: Double, b: Double) = a - b },
  TIMES("*", true) { override fun op(a: Double, b: Double) = a * b },
  DIVIDE("/", false) { override fun op(a: Double, b: Double) = a / b };

  abstract fun op(a: Double, b: Double): Double
  override fun toString() = s
}

private data class Formula(val c: Combiner, val op1: Op, val op2: Op, val op3: Op)
