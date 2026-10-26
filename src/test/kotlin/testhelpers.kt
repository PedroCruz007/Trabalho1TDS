package go

fun p(text: String, size: Int = 9): Position = Position.parse(text, size)!!

fun boardOf(black: List<String>, white: List<String>, size: Int = 9): Board {
    var b = Board.empty(size)
    black.forEach { b = b.with(p(it, size), Player.BLACK) }
    white.forEach { b = b.with(p(it, size), Player.WHITE) }
    return b
}


fun Game.ok(pos: String): Game = (play(p(pos, board.size)) as MoveResult.Success).game


fun Game.failure(pos: String): String = (play(p(pos, board.size)) as MoveResult.Failure).reason


fun Game.passOk(): Game = (pass() as MoveResult.Success).game


fun koSetup(): Game = Game(
    board = boardOf(
        black = listOf("5e", "6d", "4d", "5c"),
        white = listOf("6e", "4e", "5f")
    ),
    turn = Player.WHITE
)