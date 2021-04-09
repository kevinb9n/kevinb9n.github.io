package site.kevinb9n.tmsoloplacement

import site.kevinb9n.tmsoloplacement.MarsMap.HexArea

enum class TileType(val asText: String) { CITY("[C]"), GREENERY("[G]") }

class Board(val map: MarsMap) {
  val tiles = mutableMapOf<HexArea, TileType>()

  fun place(tile: TileType, area: HexArea) = check(tiles.put(area, tile) == null)
  fun tileOn(area: HexArea) = tiles[area]
  fun hasTile(area: HexArea) = tiles[area] != null
  fun isAvailable(area: HexArea) = !(area.isReserved() || hasTile(area))

  fun isAvailableForCity(area: HexArea) = isAvailable(area)
      && area.neighbors().none { tileOn(it) == TileType.CITY }

  fun display() {
    print(" ".repeat(38))
    for (col in 1..9) {
      printPadded("", "C$col")
    }
    println()
    print(" ".repeat(37))
    for (col in 1..9) {
      printPadded("", "/")
    }
    print("\n\n\n")
    for (row in 1..9) {
      print(" ".repeat(3 * (9 - row)))
      printPadded("R$row", "-", "", "")
      for (col in 1..9) {
        val area = map[row, col]
        if (area == null) {
          printPadded("")
        } else {
          printPadded(tileOn(area)?.asText ?: area.asText)
        }
        printPadded("")
      }
      print("\n\n")
    }
  }

  private fun printPadded(vararg strs: String) {
    for (s in strs) {
      print(when (s.length) {
        0 -> "   "
        1 -> " $s "
        2 -> "$s "
        3 -> s
        else -> throw IllegalArgumentException()
      })
    }
  }
}
