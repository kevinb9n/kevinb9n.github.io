package site.kevinb9n.tmsoloplacement

fun placeTiles(board: Board, deck: Deck) {
  val candidates1 = board.map.allAreas().filter { !it.isReserved() }
  val card1 = deck.draw()
  val city1 = candidates1[card1]
  board.place(TileType.CITY, city1)

  val candidates2 = candidates1.filter(board::isAvailableForCity)
  val card2 = deck.draw { it < candidates2.size }
  val city2 = candidates2.reversed()[card2]
  board.place(TileType.CITY, city2)

  val candidates3 = city1.neighbors().filter { board.isAvailable(it) }
  val card3 = deck.draw()
  board.place(TileType.GREENERY, getCyclic(candidates3, card3))

  val candidates4 = city2.neighbors().filter { board.isAvailable(it) }
  val card4 = deck.draw()
  board.place(TileType.GREENERY, getCyclic(candidates4, card4))
}

private fun <E> getCyclic(list: List<E>, index: Int) = list.get(index % list.size)