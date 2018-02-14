import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import mu.KotlinLogging
import org.apache.commons.io.IOUtils
import org.archive.io.warc.WARCReaderFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.IllegalArgumentException
import java.nio.charset.Charset

private const val DATA_DIR = "../utopia2-data"
private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.warn("Starting Utopia Machine")

    val printer = LinePrinter()

    Observable.just(DATA_DIR)
        .flatMap { Observable.fromIterable(listFiles(it)) }
        .map { file ->
            logger.debug { "reading archive ${file.path}" }
            WARCReaderFactory.get(file.path, FileInputStream(file), true)
        }
        .flatMap { Observable.fromIterable(it) }
        .map { record ->
            val bytes = IOUtils.toByteArray(record, record.available())
            val content = String(bytes, Charset.forName("utf-8"))
            val index = content.indexOf(string = "utopia", ignoreCase = true)
            val snippet = if (index != -1) content.snippet(index) else null
            Pair(record, snippet)
        }
        .filter { it.second != null }
        .subscribeBy(
            onNext = {
                val header = it.first.header
                val snippet = it.second

                logger.debug { "${header.url}: $snippet" }
                if (snippet != null) {
                    printer.print(header, snippet)
                }
            },
            onComplete = { logger.warn("Utopia Machine stopped") },
            onError = { logger.error("error processing archive records", it) }
        )
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

private fun listFiles(dirName: String): List<File> {
    val data = File(dirName)
    if (data.exists()) {
        val files = data.listFiles()
        if (files != null && files.isNotEmpty()) {
            logger.info { "found ${files.size} files in $dirName" }
            return files.toList()
        }
    }
    throw FileNotFoundException("$dirName does not exist or is empty, run ./gradlew downloadCrawls")
}
