import go.Board
import go.Player
import go.Position
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BoardTests {

    val sample = Board.Companion.fromRows(
        listOf(
            "##.",
            "..O",
            "..."
        )
    )

    @Test
    fun `groupAt finds connected stones of the same color`() {
        assertEquals(setOf(Position(2, 0), Position(2, 1)), sample.groupAt(Position(2, 0)))
    }

    @Test
    fun `liberties of a group`() {
        val group = sample.groupAt(Position(2, 0))
        assertEquals(
            setOf(Position(1, 0), Position(1, 1), Position(2, 2)),
            sample.liberties(group)
        )
    }

    @Test
    fun `with does not change the original board`() {
        val board = Board.Companion.empty(9)
        val updated = board.with(Position(0, 0), Player.BLACK)
        assertNull(board[Position(0, 0)])
        assertEquals(Player.BLACK, updated[Position(0, 0)])
    }

    @Test
    fun `render empty 9x9 has header and row labels`() {
        val lines = Board.Companion.empty(9).render().lines()
        assertEquals(10, lines.size)
        assertEquals("  A B C D E F G H I", lines[0])
        assertEquals("9 . . . . . . . . .", lines[1])
        assertEquals("1 . . . . . . . . .", lines[9])
    }
}