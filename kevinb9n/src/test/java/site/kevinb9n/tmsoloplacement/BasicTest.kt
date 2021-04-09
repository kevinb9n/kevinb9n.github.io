package site.kevinb9n.tmsoloplacement

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import site.kevinb9n.tmsoloplacement.MarsMap.HexArea

class BasicTest {
  @Test
  fun `try some shit`() {
    val noctis: HexArea = THARSIS.get(5, 3)!!
    val neighbors = noctis.neighbors()
    assertThat(neighbors).hasSize(6)
    // fails with (non-equal instance of same class with same string representation)
    assertThat(neighbors.filter { it.isReserved() }).containsExactly(
        HexArea(THARSIS, 5, 4, MarsMap.AreaType.NOCTIS, "WPP"))

  }
}
