import org.archive.io.ArchiveRecordHeader

class LinePrinter {
    fun print(header: ArchiveRecordHeader, snippet: String) {
        val text = "${header.url}\n$snippet\n\n\n\n\n"
        // TODO lp
    }
}
