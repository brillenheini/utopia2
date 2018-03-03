import org.archive.io.ArchiveRecordHeader
import java.io.BufferedWriter
import java.io.OutputStreamWriter

class LinePrinter {

    fun printIntro() = print("${BOLD}Utopia Machine 2.0\n\n")

    fun printSnippet(snippet: String, header: ArchiveRecordHeader) =
        print("$BOLD${header.url}\n\n$snippet\n\n\n\n\n\n")

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
        private const val BOLD = "\u001B\u0045"
    }
}
