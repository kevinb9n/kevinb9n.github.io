package site.kevinb9n.tmsoloplacement

import com.google.common.collect.HashMultiset
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import site.kevinb9n.tmsoloplacement.MarsMap.HexArea
import site.kevinb9n.tmsoloplacement.TileType.CITY
import site.kevinb9n.tmsoloplacement.TileType.GREENERY

class GameInitTest {
  @TestFactory
  internal fun `all configs`(): List<DynamicTest> {
    val result = mutableListOf<DynamicTest>()
    for (zp in ZeroPolicy.values()) {
      for (op in OverflowPolicy.values()) {
        for (sp in SkipPolicy.values().reversed()) {
          for (pm in PublishedMap.values()) {
            result += DynamicTest.dynamicTest("$zp $op $sp $pm") { exerciseConfig(pm, zp, op, sp) }
          }
        }
      }
    }
    return result
  }

  private fun exerciseConfig(
    pm: PublishedMap,
    zp: ZeroPolicy,
    op: OverflowPolicy,
    sp: SkipPolicy
  ) {
    val cityLocations = HashMultiset.create<HexArea>()
    val greeneryLocations = HashMultiset.create<HexArea>()
    for (i in 1..100_000) {
      val board = Board(pm.map)
      val deck = AllExpansionsDeck()

      val placer = NeutralTilePlacer(board, deck, zp, op, sp)
      placer.placeAllTiles()

      cityLocations.addAll(board.tiles.filterValues { it == CITY }.keys)
      greeneryLocations.addAll(board.tiles.filterValues { it == GREENERY }.keys)
    }

    val maxCity: Int = cityLocations.entrySet().maxOf { it.count }
    println("maxCity=$maxCity")

    val popularCitySpot = cityLocations.entrySet().first { it.count == maxCity }.element
    println("popularCitySpot=$popularCitySpot")

    val maxGreenery: Int = greeneryLocations.entrySet().maxOf { it.count }
    println("maxGreenery=$maxGreenery")

    val popularGreenerySpot = greeneryLocations.entrySet()
      .first { it.count == maxGreenery }.element
    println("popularGreenerySpot=$popularGreenerySpot")
  }
}
