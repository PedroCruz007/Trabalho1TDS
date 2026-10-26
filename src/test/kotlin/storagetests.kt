package go

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StorageTests {

    @Test
    fun `round trip of a new game`() {
        val game = Game.new()
        assertEquals(game, Storage.deserialize(Storage.serialize(game)))
    }

    @Test
    fun `round trip keeps captures and previous board`() {
        val game = koSetup().ok("5d")
        val loaded = Storage.deserialize(Storage.serialize(game))
        assertEquals(game, loaded)
        assertTrue(loaded.failure("5e").contains("loop"))
    }

    @Test
    fun `invalid content is rejected`() {
        assertFailsWith<IllegalArgumentException> { Storage.deserialize("isto não é um jogo") }
    }

    @Test
    fun `save and load through a real file`() {
        val name = "test_tmp_go_game"
        try {
            val game = Game.new().ok("5e").ok("5d")
            Storage.save(name, game)
            assertEquals(game, Storage.load(name))
        } finally {
            File("$name.txt").delete()
        }
    }
}