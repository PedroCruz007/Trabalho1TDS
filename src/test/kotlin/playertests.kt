import go.Player
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerTests {

    @Test
    fun `opponent of black is white`() {
        assertEquals(Player.WHITE, Player.BLACK.opponent)
    }

    @Test
    fun `opponent of white is black`() {
        assertEquals(Player.BLACK, Player.WHITE.opponent)
    }

    @Test
    fun `symbols`() {
        assertEquals('#', Player.BLACK.symbol)
        assertEquals('O', Player.WHITE.symbol)
    }

    @Test
    fun `toString matches the status line format`() {
        assertEquals("# (Black)", Player.BLACK.toString())
        assertEquals("O (White)", Player.WHITE.toString())
    }
}