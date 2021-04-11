package site.kevinb9n.tmsoloplacement

import com.google.common.math.IntMath.mod
import site.kevinb9n.tmsoloplacement.MarsMap.HexArea
import kotlin.math.max
import kotlin.math.min

class NeutralTilePlacer(
  val board: Board,
  val deck: Deck,
  val zeroPolicy: ZeroPolicy = ZeroPolicy.REDRAW,
  val overflowPolicy: OverflowPolicy = OverflowPolicy.REDRAW,
  val skipPolicy: SkipPolicy = SkipPolicy.SKIP_ALL_UNAVAIL
) {

  fun placeAllTiles() {
    val city1 = placeCityTile1()
    val city2 = placeCityTile2()
    placeGreeneryTilesBy(city1, city2)
  }

  fun placeCityTile1(): HexArea {
    val candidates = board.map.allAreas().filter { !it.isReserved() }

    // Only have to worry about the zero policy for this
    val index = zeroPolicy.adjust(drawWithZeroPolicy(deck), candidates.size)
    val cityLocation = candidates[index - 1]
    board.place(TileType.CITY, cityLocation)
    return cityLocation
  }

  fun placeCityTile2(): HexArea {
    val candidates2 = board.map.allAreas().filter { skipPolicy.isOk(board, it) }.reversed()
    val card2 = drawWithBothPolicies(deck, candidates2.size)

    var index2 = zeroPolicy.adjust(card2, candidates2.size)
    index2 = overflowPolicy.adjust(index2, candidates2.size) - 1

    // advance to legal space - if not using that policy, this won't loop anyway
    var city2: HexArea
    do {
      city2 = getCyclic(candidates2, index2++)
    } while (!board.isAvailableForCity(city2)) // I think we can't hit the edge... but we did?

    board.place(TileType.CITY, city2)
    return city2
  }

  fun placeGreeneryTilesBy(city1: HexArea, city2: HexArea) {
    val greenery2candidates = city2.neighbors().filter(board::isAvailable)

    if (greenery2candidates.size == 1) {
      val onlyPlaceForGreenery2 = greenery2candidates.single()
      if (onlyPlaceForGreenery2.isNeighbor(city1)) {
        // special case: deny this spot to the first city
        board.place(TileType.GREENERY, onlyPlaceForGreenery2)
        placeGreeneryTileBy(city1)
        // now we would place second city; we already know where it goes but we burn a card anyway
        drawWithZeroPolicy(deck)
        return
      }
    }

    placeGreeneryTileBy(city1)
    placeGreeneryTileBy(city2)
  }

  fun placeGreeneryTileBy(city: HexArea) {
    val candidates = city.neighbors().filter(board::isAvailable)
    val card = drawWithZeroPolicy(deck)
    val index = zeroPolicy.adjust(card, candidates.size) - 1
    board.place(TileType.GREENERY, getCyclic(candidates, index))
  }

  private fun drawWithZeroPolicy(deck: Deck): Int = deck.draw { zeroPolicy.acceptCardFromDeck(it) }

  private fun drawWithBothPolicies(deck: Deck, candidates: Int) = deck.draw {
    zeroPolicy.acceptCardFromDeck(it) && overflowPolicy.acceptCardFromDeck(it, candidates)
  }

  private fun <E> getCyclic(list: List<E>, index: Int) = list[mod(index, list.size)]
}

enum class ZeroPolicy {
  COUNT_FROM_ZERO { override fun adjust(cost: Int, numCandidates: Int) = cost + 1 },
  CYCLE {
    override fun adjust(cost: Int, numCandidates: Int) =
      if (cost != 0) cost else numCandidates
  },
  SATURATE { override fun adjust(cost: Int, numCandidates: Int) = max(cost, 1) },
  REDRAW {
    override fun acceptCardFromDeck(card: Int) = card > 0
    override fun adjust(cost: Int, numCandidates: Int) = cost
  };

  /** Turns a card cost into a 1-referenced index, handling the zero case. */
  abstract fun adjust(cost: Int, numCandidates: Int): Int
  open fun acceptCardFromDeck(card: Int) = true
}

enum class OverflowPolicy {
  CYCLE {
    override fun adjust(index: Int, numCandidates: Int) =
      if (index > numCandidates) index - numCandidates else index
  },
  SATURATE { override fun adjust(index: Int, numCandidates: Int) = min(index, numCandidates) },
  REDRAW {
    override fun acceptCardFromDeck(card: Int, numCandidates: Int) = card < numCandidates // acl
    override fun adjust(index: Int, numCandidates: Int) = index
  };

  /** Turns a 1-ref index that might overflow to one that mightn't */
  abstract fun adjust(index: Int, numCandidates: Int): Int
  open fun acceptCardFromDeck(card: Int, numCandidates: Int) = true
}

enum class SkipPolicy {
  SKIP_ALL_UNAVAIL { override fun isOk(board: Board, area: HexArea) = board.isAvailableForCity(area) },
  ADVANCE_IF_NEEDED { override fun isOk(board: Board, area: HexArea): Boolean = !area.isReserved() };

  abstract fun isOk(board: Board, area: HexArea): Boolean
}
