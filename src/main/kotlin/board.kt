package go


data class Board(val size: Int, private val cells: List<Player?>) {

    init {
        require(cells.size == size * size) { "Invalid number of cells" }
    }

    private fun index(pos: Position) = pos.row * size + pos.col

    fun isInside(pos: Position) = pos.row in 0 until size && pos.col in 0 until size

    operator fun get(pos: Position): Player? = cells[index(pos)]


    fun positions(): List<Position> =
        (0 until size).flatMap { r -> (0 until size).map { c -> Position(r, c) } }


    fun neighbors(pos: Position): List<Position> =
        listOf(
            Position(pos.row + 1, pos.col),
            Position(pos.row - 1, pos.col),
            Position(pos.row, pos.col + 1),
            Position(pos.row, pos.col - 1)
        ).filter { isInside(it) }


    fun with(pos: Position, player: Player?): Board =
        Board(size, cells.toMutableList().also { it[index(pos)] = player })


    fun without(positions: Collection<Position>): Board {
        val mutable = cells.toMutableList()
        positions.forEach { mutable[index(it)] = null }
        return Board(size, mutable)
    }


    fun groupAt(start: Position): Set<Position> {
        val color = this[start] ?: return emptySet()
        val group = mutableSetOf(start)
        val toVisit = ArrayDeque(listOf(start))
        while (toVisit.isNotEmpty()) {
            val current = toVisit.removeFirst()
            for (n in neighbors(current)) {
                if (this[n] == color && group.add(n)) toVisit.addLast(n)
            }
        }
        return group
    }


    fun liberties(group: Set<Position>): Set<Position> =
        group.flatMap { neighbors(it) }.filter { this[it] == null }.toSet()


    fun toRows(): List<String> =
        (size - 1 downTo 0).map { r ->
            (0 until size).map { c -> this[Position(r, c)]?.symbol ?: '.' }.joinToString("")
        }


    fun render(): String {
        val width = size.toString().length
        val header = " ".repeat(width) + " " + (0 until size).joinToString(" ") { ('A' + it).toString() }
        val rows = toRows().mapIndexed { i, row ->
            val label = (size - i).toString().padStart(width)
            "$label " + row.toCharArray().joinToString(" ")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    companion object {
        fun empty(size: Int) = Board(size, List(size * size) { null })


        fun fromRows(rows: List<String>): Board {
            val size = rows.size
            require(size > 0) { "Empty board" }
            require(rows.all { it.length == size }) { "Board must be square" }
            val cells = MutableList<Player?>(size * size) { null }
            rows.forEachIndexed { i, line ->
                val r = size - 1 - i
                line.forEachIndexed { c, ch ->
                    cells[r * size + c] = when (ch) {
                        Player.BLACK.symbol -> Player.BLACK
                        Player.WHITE.symbol -> Player.WHITE
                        '.' -> null
                        else -> throw IllegalArgumentException("Invalid board character '$ch'")
                    }
                }
            }
            return Board(size, cells)
        }
    }
}