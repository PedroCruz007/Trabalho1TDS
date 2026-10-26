package go


enum class Player(val symbol: Char, val label: String) {
    BLACK('#', "Black"),
    WHITE('O', "White");

    val opponent: Player
        get() = if (this == BLACK) WHITE else BLACK


    override fun toString(): String = "$symbol ($label)"
}