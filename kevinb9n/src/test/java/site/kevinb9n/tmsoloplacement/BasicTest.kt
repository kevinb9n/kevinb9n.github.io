package site.kevinb9n.tmsoloplacement

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BasicTest {
  class FakeDeck(vararg val cardsInOrder: Int) : Deck {
    private var iterator: MutableIterator<Int> = cardsInOrder.toMutableList().iterator()
    override fun draw(): Int {
      val next = iterator.next()
      iterator.remove()
      return next
    }
  }

  @Test
  fun `simple common case`() {
    val board = Board(PublishedMaps.THARSIS.map)
    val deck = FakeDeck(11, 8, 10, 12)
    val placer = NeutralTilePlacer(board, deck) // redraw/redraw/skip-all

    placer.placeAllTiles()

    assertThat(board.tileAt(3, 4)).isEqualTo(TileType.CITY)
    assertThat(board.tileAt(8, 6)).isEqualTo(TileType.CITY)
    assertThat(board.tileAt(4, 5)).isEqualTo(TileType.GREENERY)
    assertThat(board.tileAt(8, 5)).isEqualTo(TileType.GREENERY)
  }
}
