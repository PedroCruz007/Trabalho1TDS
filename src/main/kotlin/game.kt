package go


sealed class MoveResult {
    data class Success(val game: Game) : MoveResult()
    data class Failure(val reason: String) : MoveResult()
}


data class Score(val black: Double, val white: Double) {
    val winner: Player?
        get() = when {
            black > white -> Player.BLACK
            white > black -> Player.WHITE
            else -> null
        }
}

data class Game(
    val board: Board,
    val turn: Player = Player.BLACK,
    val blackCaptures: Int = 0,
    val whiteCaptures: Int = 0,
    val previousBoard: Board? = null,
    val lastPassed: Boolean = false,
    val finished: Boolean = false
) {
    fun capturesOf(player: Player) = if (player == Player.BLACK) blackCaptures else whiteCaptures

    fun play(pos: Position): MoveResult {
        if (finished) return MoveResult.Failure("The game is over.")
        if (!board.isInside(pos)) return MoveResult.Failure("Position outside the board.")
        if (board[pos] != null) return MoveResult.Failure("Position ${pos.toText()} is already occupied.")

        val player = turn
        var newBoard = board.with(pos, player)

        // 1. Capturar grupos adversários sem liberdades
        var captured = 0
        for (n in newBoard.neighbors(pos)) {
            if (newBoard[n] == player.opponent) {
                val group = newBoard.groupAt(n)
                if (newBoard.liberties(group).isEmpty()) {
                    newBoard = newBoard.without(group)
                    captured += group.size
                }
            }
        }

        // 2. Suicídio: o grupo da nova pedra tem de ter liberdades
        if (newBoard.liberties(newBoard.groupAt(pos)).isEmpty())
            return MoveResult.Failure("Illegal move: suicide (no liberties).")

        // 3. Loop: não pode recriar o estado anterior
        if (newBoard == previousBoard)
            return MoveResult.Failure("Illegal move: it would recreate the previous board (loop).")

        return MoveResult.Success(
            copy(
                board = newBoard,
                turn = player.opponent,
                blackCaptures = blackCaptures + if (player == Player.BLACK) captured else 0,
                whiteCaptures = whiteCaptures + if (player == Player.WHITE) captured else 0,
                previousBoard = board,
                lastPassed = false
            )
        )
    }


    fun pass(): MoveResult {
        if (finished) return MoveResult.Failure("The game is over.")
        return MoveResult.Success(
            copy(
                turn = turn.opponent,
                previousBoard = board,
                lastPassed = true,
                finished = lastPassed
            )
        )
    }


    fun score(): Score {
        var blackTerritory = 0
        var whiteTerritory = 0
        val visited = mutableSetOf<Position>()

        for (start in board.positions()) {
            if (board[start] != null || start in visited) continue
            val region = mutableSetOf(start)
            val borders = mutableSetOf<Player>()
            val toVisit = ArrayDeque(listOf(start))
            visited.add(start)
            while (toVisit.isNotEmpty()) {
                val current = toVisit.removeFirst()
                for (n in board.neighbors(current)) {
                    val stone = board[n]
                    if (stone != null) borders.add(stone)
                    else if (visited.add(n)) {
                        region.add(n)
                        toVisit.addLast(n)
                    }
                }
            }
            if (borders.size == 1) {
                if (borders.first() == Player.BLACK) blackTerritory += region.size
                else whiteTerritory += region.size
            }
        }

        return Score(
            black = blackCaptures + blackTerritory - komi(board.size),
            white = (whiteCaptures + whiteTerritory).toDouble()
        )
    }

    companion object {
        const val DEFAULT_SIZE = 9
        val VALID_SIZES = listOf(19, 13, 9)

        fun komi(size: Int): Double = when (size) {
            19 -> 5.5
            13 -> 4.5
            else -> 3.5
        }

        fun new(size: Int = DEFAULT_SIZE) = Game(Board.empty(size))
    }
}