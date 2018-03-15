package print

import org.junit.Test

import org.junit.Assert.*

class HighlighterTest {
    @Test
    fun highlightCaseInsensitive() {
        val terms = listOf("a")
        assertEquals(
            "${UNDERLINE_START}A${UNDERLINE_END}bb${UNDERLINE_START}a$UNDERLINE_END",
            "Abba".highlight(terms)
        )
    }

    @Test
    fun highlightList() {
        val terms = listOf("utopia", "utopie")
        assertEquals(
            "asdf${UNDERLINE_START}Utopia$UNDERLINE_END ${UNDERLINE_START}utopie$UNDERLINE_END xyz",
            "asdfUtopia utopie xyz".highlight(terms)
        )
    }
}
