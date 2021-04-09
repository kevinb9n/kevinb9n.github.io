package site.kevinb9n.tmsoloplacement

fun main() {
  val map = listOf(THARSIS, HELLAS, ELYSIUM).random()
  val board = Board(map)
  placeTiles(board, Deck)

  println("Map chosen: ${map.name}")
  println("Cards drawn: ${Deck.cardsDrawn}")
  println()
  board.display()
}