package site.kevinb9n.tmsoloplacement

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import site.kevinb9n.tmsoloplacement.MarsMap.HexArea

class BasicTest {
  @Test
  fun `try some shit`() {
    val noctis : HexArea = THARSIS.get(5, 3)!!
    val neighbors = THARSIS.neighbors(noctis)
    assertThat(neighbors).hasSize(6)
    assertThat(neighbors.filter { it.isReserved }).containsExactly(HexArea(5, 4, true, "PP"))

  }
}