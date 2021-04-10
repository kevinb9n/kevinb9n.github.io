package site.kevinb9n.tmsoloplacement

import com.google.common.collect.HashMultiset

fun main() {
  for (pm in PublishedMap.values()) {
    for (sp in SkipPolicy.values().reversed()) {
      for (zp in ZeroPolicy.values()) {
        for (op in OverflowPolicy.values()) {
          val cityLocations = HashMultiset.create<MarsMap.HexArea>()
          val greeneryLocations = HashMultiset.create<MarsMap.HexArea>()
          for (i in 1..100000) {
            val board = Board(pm.map)
            val deck = AllExpansionsDeck()

            val placer = NeutralTilePlacer(board, deck, zp, op, sp)
            try {
              placer.placeAllTiles()
            } catch (e: Exception) {
              e.printStackTrace()
              println("$zp $op $sp $pm ${deck.cardsDrawn}")
              continue
            }

            cityLocations.addAll(board.tiles.filterValues { it == TileType.CITY }.keys)
            greeneryLocations.addAll(board.tiles.filterValues { it == TileType.GREENERY }.keys)
          }
          val maxCity : Int = cityLocations.entrySet().map { it.count }.maxOrNull()!!
          val popularCitySpot = cityLocations.entrySet().filter { it.count == maxCity }.first().element
          val maxGreenery : Int = greeneryLocations.entrySet().map { it.count }.maxOrNull()!!
          val popularGreenerySpot = greeneryLocations.entrySet().filter { it.count ==
              maxGreenery }.first().element
          println("$zp $op $sp $pm $popularCitySpot $maxCity $popularGreenerySpot $maxGreenery")
        }
      }
    }
  }
}
