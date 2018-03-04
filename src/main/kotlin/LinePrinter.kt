import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.URI

class LinePrinter(private val debug: Boolean) {

    fun printIntro() {
        print("${BOLD_START}Utopia Machine 2.0\n\n$BOLD_END")
        printSnippet(INTRO_URI, INTRO_SNIPPET)
    }

    fun printSnippet(uri: URI, snippet: String) = print("$uri\n\n$snippet\n\n\n")

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
        private const val BOLD_START = "\u001B\u0045"
        private const val BOLD_END = "\u001B\u0046"
        private const val UNDERLINE_START = "\u001b\u002d\u0001"
        private const val UNDERLINE_END = "\u001b\u002d\u0000"

        private val INTRO_URI = URI("https://de.wikipedia.org/wiki/Utopie")
        private val INTRO_SNIPPET = """
            |Eine Utopie ist der Entwurf einer fiktiven Gesellschaftsordnung, die nicht an zeitgenössische historisch-kulturelle Rahmenbedingungen gebunden ist.
            |
            |Der Begriff bezieht sich auf „Nicht-Ort“; aus altgriechisch οὐ- ou- „nicht-“ und τόπος tópos „Ort“. Die mit Utopie beschriebene fiktive Gesellschaftsordnung ist meist positiv. Deshalb handelt es sich in dem Sinne um ein Sprachspiel zwischen Utopie und Eutopie aus εὖ (eu) „gut“ und τόπος. Dagegen bezeichnet die Dystopie die pessimistische Beschreibung einer unethisch negativen Gesellschaftsordnung.
            """.trimMargin()
    }
}
