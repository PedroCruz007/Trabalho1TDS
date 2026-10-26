package go



fun printGame(game: Game) {
    println(game.board.render())
    val status = "Captures: #=${game.blackCaptures} - O=${game.whiteCaptures}"
    if (game.finished) {
        val score = game.score()
        println("Game over  $status")
        println("Final score: #=${score.black} - O=${score.white}")
        val winner = score.winner
        println(if (winner == null) "Result: draw" else "Winner: $winner")
    } else {
        println("Turn: ${game.turn}  $status")
    }
}

fun handle(result: MoveResult, current: Game): Game =
    when (result) {
        is MoveResult.Success -> result.game.also { printGame(it) }
        is MoveResult.Failure -> current.also { println("Error: ${result.reason}") }
    }

fun main() {
    var game = Game.new()
    printGame(game)

    while (true) {
        print("> ")
        val line = readlnOrNull() ?: break
        val parts = line.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (parts.isEmpty()) continue
        val args = parts.drop(1)

        when (parts[0].lowercase()) {
            "new" -> {
                val size = if (args.isEmpty()) Game.DEFAULT_SIZE else args[0].toIntOrNull()
                if (size == null || size !in Game.VALID_SIZES) {
                    println("Error: size must be one of ${Game.VALID_SIZES.sorted().joinToString(", ")}.")
                } else {
                    game = Game.new(size)
                    printGame(game)
                }
            }

            "play" -> {
                if (args.size != 1) {
                    println("Usage: play <position>   (e.g. play 3f)")
                } else {
                    val pos = Position.parse(args[0], game.board.size)
                    if (pos == null) println("Error: invalid position '${args[0]}'.")
                    else game = handle(game.play(pos), game)
                }
            }

            "pass" -> game = handle(game.pass(), game)

            "save" -> {
                if (args.size != 1) println("Usage: save <name>")
                else try {
                    Storage.save(args[0], game)
                    println("Game saved to '${args[0]}.txt'.")
                    printGame(game)
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }

            "load" -> {
                if (args.size != 1) println("Usage: load <name>")
                else try {
                    game = Storage.load(args[0])
                    printGame(game)
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }

            "exit" -> return

            else -> println("Unknown command '${parts[0]}'. Commands: new [size], play <pos>, pass, save <name>, load <name>, exit")
        }
    }
}