package site.kevinb9n.tmsoloplacement

interface Deck {
  fun draw(): Int

  fun draw(accept: (Int) -> Boolean): Int {
    while (true) {
      val next = draw()
      if (accept(next)) return next
    }
  }
}
