package site.kevinb9n.tmsoloplacement

import com.google.common.base.Splitter

class MarsMap(val name: String, vararg textRows: String) {
  // pad!
  private val grid = Array(11) { arrayOfNulls<HexArea?>(11) }
  private val allAreas = mutableListOf<HexArea>()

  init {
    var row = 0
    for (line in textRows) {
      row++ // pad!
      var col = 0
      val splitter = Splitter.fixedLength(6).trimResults()
      for (code in splitter.split(line)) {
        col++ // pad!
        if (code.isNotEmpty()) {
          val hexArea = HexArea(this, row, col, AreaType.forChar(code[0]), code)
          grid[row][col] = hexArea
          allAreas.add(hexArea)
        }
      }
    }
  }

  operator fun get(row: Int, col: Int) = grid[row][col]

  fun allAreas() = allAreas.toList()

  enum class AreaType(val c: Char, val isReserved: Boolean) {
    WATER('W', true),
    LAND('L', false),
    VOLCANIC('V', false),
    NOCTIS('N', true),
    ;

    companion object {
      fun forChar(c: Char): AreaType {
        for (type in values())
          if (type.c == c) return type
        throw IllegalArgumentException()
      }
    }
  }

  data class HexArea(
      val map: MarsMap, val row: Int, val col: Int,
      val type: AreaType, val asText: String) {

    fun isReserved() = type.isReserved
    override fun toString() = "($row, $col) [$asText]"

    fun isNeighbor(area: HexArea): Boolean {
      require(area.map == map)
      val rowDist = row - area.row
      val colDist = col - area.col
      return rowDist in -1..1 && colDist in -1..1 && rowDist + colDist != 0
    }

    // returns neighboring hex areas that actually exist, clockwise from top-left
    fun neighbors(): List<HexArea> = listOf(
        map[row - 1, col - 1],
        map[row - 1, col + 0],
        map[row + 0, col + 1],
        map[row + 1, col + 1],
        map[row + 1, col + 0],
        map[row + 0, col - 1]).filterNotNull()
  }
}
