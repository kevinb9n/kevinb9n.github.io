package site.kevinb9n.tmsoloplacement

import com.google.common.math.IntMath.mod
import site.kevinb9n.tmsoloplacement.MarsMap.HexArea
import kotlin.math.max
import kotlin.math.min

enum class ZeroPolicy {
  COUNT_FROM_ZERO {
    override fun adjust(cost: Int, numCandidates: Int) = cost + 1
  },
  CYCLE {
    override fun adjust(cost: Int, numCandidates: Int) =
      if (cost != 0) cost else numCandidates
  },
  SATURATE {
    override fun adjust(cost: Int, numCandidates: Int) = max(cost, 1)
  },
  REDRAW {
    override fun acceptCardFromDeck(card : Int) = card > 0
    override fun adjust(cost: Int, numCandidates: Int) = cost
  };

  /** Turns a card cost into a 1-referenced index, handling the zero case. */
  abstract fun adjust(cost: Int, numCandidates: Int): Int
  open fun acceptCardFromDeck(card : Int) = true
}

enum class OverflowPolicy {
  CYCLE {
    override fun adjust(index: Int, numCandidates: Int) =
      if (index > numCandidates) index - numCandidates else index
  },
  SATURATE {
    override fun adjust(index: Int, numCandidates: Int) = min(index, numCandidates)
  },
  REDRAW {
    override fun acceptCardFromDeck(card : Int, numCandidates: Int) = card < numCandidates // acl
    override fun adjust(index: Int, numCandidates: Int) = index
  },
  ;

  /** Turns a 1-ref index that might overflow to one that mightn't */
  abstract fun adjust(index : Int, numCandidates : Int): Int
  open fun acceptCardFromDeck(card : Int, numCandidates : Int) = true
}

enum class SkipPolicy { SKIP_ALL_UNAVAIL, ADVANCE_IF_NEEDED }

class NeutralTilePlacer(
  val zeroPolicy: ZeroPolicy,
  val overflowPolicy: OverflowPolicy,
  val skipPolicy: SkipPolicy) {

  fun placeTiles(board: Board, deck: Deck) {
    val candidates1 = board.map.allAreas().filter { !it.isReserved() }

    // Only have to worry about the zero policy for this
    val card1 = deck.draw { zeroPolicy.acceptCardFromDeck(it) }
    val index1 = zeroPolicy.adjust(card1, candidates1.size)
    val city1 = candidates1[index1]
    board.place(TileType.CITY, city1)

    val candidates2 = when (skipPolicy) {
      SkipPolicy.SKIP_ALL_UNAVAIL -> candidates1.filter(board::isAvailableForCity)
      else -> candidates1
    }.reversed()

    val card2 = draw(deck, candidates2.size)

    var index2 = zeroPolicy.adjust(card2, candidates2.size)
    index2 = overflowPolicy.adjust(card2, candidates2.size)

    // advance to legal space - if not using that policy this won't do anything
    var city2 : HexArea
    do {
      city2 = candidates2[index2++]
    } while (!board.isAvailableForCity(city2)) // I think we can't hit the edge

    board.place(TileType.CITY, city2)

    val candidates3 = city1.neighbors().filter { board.isAvailable(it) }
    val card3 = draw(deck, candidates3.size)

    board.place(TileType.GREENERY, getCyclic(candidates3, card3))

    val candidates4 = city2.neighbors().filter { board.isAvailable(it) }
    val card4 = deck.draw()
    board.place(TileType.GREENERY, getCyclic(candidates4, card4))
  }

  private fun draw(deck: Deck, candidates: Int): Int {
    return deck.draw {
      zeroPolicy.acceptCardFromDeck(it)
        && overflowPolicy.acceptCardFromDeck(it, candidates)
    }
  }

  private fun drawCyclic(deck: Deck, candidates: Int): Int {
    return deck.draw {
      zeroPolicy.acceptCardFromDeck(it)
    }
  }
    private fun <E> getCyclic(list: List<E>, index: Int) =
      list.get(mod(index, list.size))
}