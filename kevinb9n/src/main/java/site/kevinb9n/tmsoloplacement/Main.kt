package site.kevinb9n.tmsoloplacement

fun main() {
  val map = PublishedMaps.values().random()
  val board = Board(map.map)
  val deck = AllExpansionsDeck()

  val placer = NeutralTilePlacer(board, deck,
      ZeroPolicy.COUNT_FROM_ZERO, OverflowPolicy.REDRAW, SkipPolicy.SKIP_ALL_UNAVAIL)

  placer.placeAllTiles()

  println("Map chosen: $map")
  println("Cards drawn: ${deck.cardsDrawn}")
  println()
  board.display()
}
