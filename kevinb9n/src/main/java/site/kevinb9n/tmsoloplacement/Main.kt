package site.kevinb9n.tmsoloplacement

fun main() {
  val map = listOf(THARSIS, HELLAS, ELYSIUM).random()
  val board = Board(map)
  val deck = Deck()

  val placer = NeutralTilePlacer(
      ZeroPolicy.COUNT_FROM_ZERO,
      OverflowPolicy.REDRAW,
      SkipPolicy.SKIP_ALL_UNAVAIL)
  placer.placeTiles(board, deck)

  println("Map chosen: ${map.name}")
  println("Cards drawn: ${deck.cardsDrawn}")
  println()
  board.display()
}
