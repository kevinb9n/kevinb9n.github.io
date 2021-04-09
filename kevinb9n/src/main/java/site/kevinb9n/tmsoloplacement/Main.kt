package site.kevinb9n.tmsoloplacement

import com.google.common.math.IntMath.gcd
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun main() {
  val map = listOf(THARSIS, HELLAS, ELYSIUM).random()
  val board = Board(map)
  placeTiles(board, Deck)

  println("Map chosen: ${map.name}")
  println("Cards drawn: ${Deck.cardsDrawn}")
  println()
  board.display()
}