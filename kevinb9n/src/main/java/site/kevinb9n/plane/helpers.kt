package site.kevinb9n.plane

fun <T> cyclicGet(list: List<T>, i: Int): T {
  return list[i % list.size]
}
