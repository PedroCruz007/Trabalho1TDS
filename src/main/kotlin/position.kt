package go


data class Position(val row: Int, val col: Int) {


    fun toText(): String = "${row + 1}${'a' + col}"

    companion object {
        private val NUMBER_LETTER = Regex("^(\\d{1,2})([a-zA-Z])$")
        private val LETTER_NUMBER = Regex("^([a-zA-Z])(\\d{1,2})$")


        fun parse(text: String, size: Int): Position? {
            val t = text.trim()
            val (number, letter) = when {
                NUMBER_LETTER.matches(t) -> NUMBER_LETTER.find(t)!!.destructured.let { it.component1() to it.component2() }
                LETTER_NUMBER.matches(t) -> LETTER_NUMBER.find(t)!!.destructured.let { it.component2() to it.component1() }
                else -> return null
            }
            val row = number.toInt() - 1
            val col = letter.lowercase()[0] - 'a'
            return if (row in 0 until size && col in 0 until size) Position(row, col) else null
        }
    }
}