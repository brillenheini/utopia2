import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import mu.KotlinLogging
import org.apache.commons.io.IOUtils
import org.archive.io.warc.WARCReaderFactory
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset

private val logger = KotlinLogging.logger {}

fun createArchiveSearcher(archive: File, query: List<String>): Flowable<SearchRecord> {
    return Flowable.create<SearchRecord>(
        { emitter ->
            logger.info { "reading archive ${archive.path}" }
            val archiveReader = WARCReaderFactory.get(archive.path, FileInputStream(archive), true)

            emitter.setCancellable {
                logger.debug { "closing archive $archive" }
                archiveReader.close()
            }

            archiveReader.forEach { record ->
                val bytes = IOUtils.toByteArray(record, record.available())
                val content = String(bytes, Charset.forName("utf-8"))
                val index = content.search(query)
                val snippet = if (index != -1) content.snippet(index) else null
                val searchRecord = SearchRecord(record.header?.url, snippet)
                emitter.onNext(searchRecord)
            }
            emitter.onComplete()
        },
        BackpressureStrategy.DROP
    )
        .filter { it.url != null && it.snippet != null }
}

/**
 * Search this String for occurrences of [query] Strings
 * and return the index if something was found or -1 otherwise.
 */
private fun String.search(query: List<String>): Int {
    var index = -1
    for (it in query) {
        index = this.indexOf(string = it, ignoreCase = true)
        if (index != -1) break
    }
    return index
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
