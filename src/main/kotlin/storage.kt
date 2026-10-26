package go


import java.io.File

object Storage {

    val VALID_NAME = Regex("^[A-Za-z0-9_-]+$")

    fun fileFor(name: String): File {
        require(VALID_NAME.matches(name)) { "Invalid name (use letters, digits, '_' or '-')." }
        return File("$name.txt")
    }

    fun save(name: String, game: Game) {
        fileFor(name).writeText(serialize(game))
    }

    fun load(name: String): Game {
        val file = fileFor(name)
        require(file.exists()) { "File '${file.name}' not found." }
        return deserialize(file.readText())
    }

    fun serialize(game: Game): String = buildString {
        appendLine("size=${game.board.size}")
        appendLine("turn=${game.turn.name}")
        appendLine("blackCaptures=${game.blackCaptures}")
        appendLine("whiteCaptures=${game.whiteCaptures}")
        appendLine("lastPassed=${game.lastPassed}")
        appendLine("finished=${game.finished}")
        appendLine("board:")
        game.board.toRows().forEach { appendLine(it) }
        appendLine("previous:")
        val previous = game.previousBoard
        if (previous == null) appendLine("none") else previous.toRows().forEach { appendLine(it) }
    }

    fun deserialize(text: String): Game {
        try {
            val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
            val boardIdx = lines.indexOf("board:")
            require(boardIdx >= 0) { "missing 'board:'" }
            val props = lines.take(boardIdx).associate {
                val parts = it.split("=", limit = 2)
                require(parts.size == 2) { "bad line '$it'" }
                parts[0] to parts[1]
            }
            fun prop(key: String) = props[key] ?: throw IllegalArgumentException("missing '$key'")

            val size = prop("size").toInt()
            require(size > 0) { "bad size" }
            val boardRows = lines.subList(boardIdx + 1, boardIdx + 1 + size)
            val board = Board.fromRows(boardRows)
            require(board.size == size) { "board size mismatch" }

            val prevIdx = boardIdx + 1 + size
            require(lines.getOrNull(prevIdx) == "previous:") { "missing 'previous:'" }
            val previous =
                if (lines.getOrNull(prevIdx + 1) == "none") null
                else Board.fromRows(lines.subList(prevIdx + 1, prevIdx + 1 + size)).also {
                    require(it.size == size) { "previous board size mismatch" }
                }

            return Game(
                board = board,
                turn = Player.valueOf(prop("turn")),
                blackCaptures = prop("blackCaptures").toInt(),
                whiteCaptures = prop("whiteCaptures").toInt(),
                previousBoard = previous,
                lastPassed = prop("lastPassed").toBooleanStrict(),
                finished = prop("finished").toBooleanStrict()
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("Corrupted or invalid game file (${e.message}).")
        }
    }
}