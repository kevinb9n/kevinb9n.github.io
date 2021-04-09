package site.kevinb9n.tmsoloplacement

import com.google.common.collect.ImmutableMultiset
import com.google.common.collect.Multiset
import kotlin.random.Random

data class Deck(val seed: Long = System.currentTimeMillis()) {
  var shuffledCards = shuffle()
  val cardsDrawn = mutableListOf<Int>()

  private fun shuffle() : MutableIterator<Int> {
    val cards : Multiset<Int> = ImmutableMultiset.builder<Int>()
      .add(0, 5)   .add(1, 10)
      .add(2, 9)   .add(3, 15)
      .add(4, 20)  .add(5, 18)
      .add(6, 20)  .add(7, 21)
      .add(8, 23)  .add(9, 20)
      .add(10, 22) .add(11, 27)
      .add(12, 22) .add(13, 20)
      .add(14, 10) .add(15, 14)
      .add(16, 9)  .add(17, 7)
      .add(18, 12) .add(20, 9)
      .add(21, 7)  .add(22, 4)
      .add(23, 10) .add(24, 3)
      .add(25, 4)  .add(26, 4)
      .add(27, 5)  .add(31, 4)
      .add(36, 2)
      // and all the singletons
      .add(19, 28, 29, 30, 32, 33, 35, 41, 43)
      .build()

    val list = cards.toMutableList()
    list.shuffle(Random(seed))
    return list.iterator()
  }

  fun draw() : Int {
    val next = shuffledCards.next()
    shuffledCards.remove()
    cardsDrawn.add(next)
    return next
  }

  fun draw(accept : (Int) -> Boolean) : Int {
    while (true) {
      val next = draw()
      if (accept(next)) return next
    }
  }
}