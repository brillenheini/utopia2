package print

import org.junit.Test

import org.junit.Assert.*

class HighlighterTest {
    @Test
    fun highlightCaseInsensitive() {
        val terms = listOf("a")
        assertEquals(
            "${ASTERISKS}A${ASTERISKS}bb${ASTERISKS}a$ASTERISKS",
            "Abba".highlight(terms)
        )
    }

    @Test
    fun highlightList() {
        val terms = listOf("utopia", "utopie")
        assertEquals(
            "asdf${ASTERISKS}Utopia$ASTERISKS ${ASTERISKS}utopie$ASTERISKS xyz",
            "asdfUtopia utopie xyz".highlight(terms)
        )
    }
}
