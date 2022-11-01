package site.kevinb9n.music

import com.google.common.math.IntMath

inline fun <reified T : Enum<T>> cyclicPlus(start: T, distance: Int): T {
  val enumValues = enumValues<T>()
  return enumValues[(start.ordinal + distance).mod(enumValues.size)]
}

inline fun <reified T : Enum<T>> cyclicMinus(start: T, distance: Int) =
  cyclicPlus(start, -distance)

inline fun <reified T : Enum<T>> cyclicMinus(start: T, end: T) =
  (start.ordinal - end.ordinal).mod(enumValues<T>().size)
