package site.kevinb9n.tmsoloplacement

import com.google.common.collect.ImmutableMultiset
import com.google.common.collect.Multiset
import kotlin.random.Random

data class Deck(val seed: Long = System.currentTimeMillis()) {
  var shuffledCards = shuffle()
  val cardsDrawn = mutableListOf<Int>()

  private fun shuffle(): MutableIterator<Int> {
    // Use all expansions and promos as of 2021-03
    val cards = ImmutableMultiset.builder<Int>()
        .addCopies(0, 5).addCopies(1, 10).addCopies(2, 9).addCopies(3, 15).addCopies(4, 20)
        .addCopies(5, 18).addCopies(6, 20).addCopies(7, 21).addCopies(8, 23).addCopies(9, 20)
        .addCopies(10, 22).addCopies(11, 27).addCopies(12, 22).addCopies(13, 20).addCopies(14, 10)
        .addCopies(15, 14).addCopies(16, 9).addCopies(17, 7).addCopies(18, 12).addCopies(20, 9)
        .addCopies(21, 7).addCopies(22, 4).addCopies(23, 10).addCopies(24, 3).addCopies(25, 4)
        .addCopies(26, 4).addCopies(27, 5).addCopies(31, 4).addCopies(36, 2)
        .add(19, 28, 29, 30, 32, 33, 35, 41, 43)
        .build()

    val list = cards.toMutableList()
    check(list.size == 365, {"list was size ${list.size}"})
    list.shuffle(Random(seed))
    return list.iterator()
  }

  fun draw(): Int {
    val next = shuffledCards.next()
    shuffledCards.remove()
    cardsDrawn.add(next)
    return next
  }

  fun draw(accept: (Int) -> Boolean): Int {
    while (true) {
      val next = draw()
      if (accept(next)) return next
    }
  }
}
