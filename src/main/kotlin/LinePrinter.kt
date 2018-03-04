import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.URI

class LinePrinter {

    fun printIntro() = print("${BOLD_START}Utopia Machine 2.0\n\n$BOLD_END")

    fun printSnippet(uri: URI, snippet: String) =
        print("$BOLD_START$uri\n\n$snippet\n\n\n\n\n\n$BOLD_END")

    private fun print(text: String) {
        val process = ProcessBuilder("lp", "-")
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

    companion object {
        // Switching our printer to bold wastes less paper
        private const val BOLD_START = "\u001B\u0045"
        private const val BOLD_END = "\u001B\u0046"
        private const val UNDERLINE_START = "\u001b\u002d\u0001"
        private const val UNDERLINE_END = "\u001b\u002d\u0000"
    }
}
