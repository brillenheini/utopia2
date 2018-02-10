import mu.KotlinLogging
import org.apache.commons.io.IOUtils
import org.archive.io.warc.WARCReaderFactory
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.nio.charset.Charset

private const val FILE = "data/CC-MAIN-20131204131715-00000-ip-10-33-133-15.ec2.internal.warc.wet.gz"
private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.warn("Starting Utopia Machine")

    val fis = FileInputStream(FILE)
    val archiveReader = WARCReaderFactory.get(FILE, fis, true)

    for (record in archiveReader) {
        val header = record.header

        val bytes = IOUtils.toByteArray(record, record.available())
        val content = String(bytes, Charset.forName("utf-8"))
        val index = content.indexOf(string = "utopia", ignoreCase = true)
        if (index != -1) {
            val snippet = content.snippet(index)
            logger.debug { "${header.url}: $snippet" }
        }
    }

    logger.warn("Utopia Machine stopped")
}

/**
 * Return a snippet of this String around [index] constrained by [before] and [after] characters
 * or this String's limits.
 */
private fun String.snippet(index: Int, before: Int = 100, after: Int = 100): String {
    if (index < 0) {
        throw IllegalArgumentException("index must be >= 0")
    }
    val start = Math.max(index - before, 0)
    val end = Math.min(index + after, this.length)
    return this.substring(start, end)
}
