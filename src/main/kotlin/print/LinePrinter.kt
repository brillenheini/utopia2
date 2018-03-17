package print

import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.URI

class LinePrinter(private val debug: Boolean, private val searchTerms: List<String>) {

    fun printIntro() {
        print("$INIT${BOLD_START}Utopia Machine 2.0$BOLD_END\n\n")
        printSnippet(INTRO_URI, INTRO_SNIPPET)
    }

    fun printSnippet(uri: URI, snippet: String) {
        val highlighted = snippet.highlight(searchTerms)
        print("$uri\n\n$highlighted\n\n\n")
    }

    private fun print(text: String) {
        if (!debug) {
            // Print with custom page size A5
            val process = ProcessBuilder("lp", "-o media=Custom.148x210mm", "-")
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .start()

            val out = BufferedWriter(OutputStreamWriter(process.outputStream, "UTF-8"))
            try {
                out.write(text)
            } finally {
                out.flush()
                out.close()
            }

            process.waitFor()
        }
    }

    companion object {
        private val INTRO_URI = URI("https://de.wikipedia.org/wiki/Utopie")
        private val INTRO_SNIPPET = """
            |Eine Utopie ist der Entwurf einer fiktiven Gesellschaftsordnung, die nicht an zeitgenössische historisch-kulturelle Rahmenbedingungen gebunden ist.
            |
            |Der Begriff bezieht sich auf „Nicht-Ort“; aus altgriechisch οὐ- ou- „nicht-“ und τόπος tópos „Ort“. Die mit Utopie beschriebene fiktive Gesellschaftsordnung ist meist positiv. Deshalb handelt es sich in dem Sinne um ein Sprachspiel zwischen Utopie und Eutopie aus εὖ (eu) „gut“ und τόπος. Dagegen bezeichnet die Dystopie die pessimistische Beschreibung einer unethisch negativen Gesellschaftsordnung.
            """.trimMargin()
    }
}
