import go.Position
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PositionTests {

    @Test
    fun `parse number then letter`() {
        assertEquals(Position(2, 5), Position.Companion.parse("3f", 9))
    }

    @Test
    fun `parse letter then number`() {
        assertEquals(Position(2, 5), Position.Companion.parse("f3", 9))
    }

    @Test
    fun `parse rejects positions outside the board`() {
        assertNull(Position.Companion.parse("10a", 9))
        assertNull(Position.Companion.parse("3j", 9))
        assertNull(Position.Companion.parse("0a", 9))
    }

    @Test
    fun `toText gives number then letter`() {
        assertEquals("3f", Position(2, 5).toText())
    }
}