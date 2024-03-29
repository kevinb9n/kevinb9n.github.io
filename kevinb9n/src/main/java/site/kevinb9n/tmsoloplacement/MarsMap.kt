package site.kevinb9n.tmsoloplacement

import com.google.common.base.Splitter

class MarsMap(text: String) {
  private val grid: Array<Array<HexArea?>>
  private val allAreas: List<HexArea>

  init {
    val textRows = text.trimMargin("/").split("""\n\s*\n""".toRegex())
    val splitter = Splitter.fixedLength(6).trimResults()
    grid = Array(11) { arrayOfNulls<HexArea?>(11) }
    allAreas = mutableListOf()
    var row = 0
    for (line in textRows) {
      row++
      var col = 0
      for (code in splitter.split(line)) {
        if (code.isNotEmpty()) {
          val hexArea = HexArea(this, row, col, AreaType.forChar(code[0]), code)
          grid[row][col] = hexArea
          allAreas.add(hexArea)
          //println("putting in $row, $col: $code")
        }
        col++
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
      fun forChar(c: Char) = values().first { it.c == c }
    }
  }

  data class HexArea(
    val map: MarsMap,
    val row: Int,
    val col: Int,
    val type: AreaType,
    val asText: String
  ) {

    fun isReserved() = type.isReserved
    override fun toString() = "($row, $col) [$asText]"

    fun isNeighbor(area: HexArea): Boolean {
      require(area.map == map)
      val rowDist = row - area.row
      val colDist = col - area.col
      return rowDist in -1..1 && colDist in -1..1 && rowDist + colDist != 0
    }

    // returns neighboring hex areas that actually exist, clockwise from top-left
    fun neighbors(): List<HexArea> = listOfNotNull(
      map[row - 1, col - 1],
      map[row - 1, col + 0],
      map[row + 0, col + 1],
      map[row + 1, col + 1],
      map[row + 1, col + 0],
      map[row + 0, col - 1]
    )
  }
}
