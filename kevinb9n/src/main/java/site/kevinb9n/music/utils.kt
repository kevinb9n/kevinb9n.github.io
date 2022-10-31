package site.kevinb9n.music

import com.google.common.math.IntMath

inline fun <reified T : Enum<T>> cyclicPlus(start: T, distance: Int): T {
  val enumValues = enumValues<T>()
  return enumValues[IntMath.mod(start.ordinal + distance, enumValues.size)]
}

inline fun <reified T : Enum<T>> cyclicMinus(start: T, end: T) =
        IntMath.mod(start.ordinal - end.ordinal, enumValues<T>().size)
