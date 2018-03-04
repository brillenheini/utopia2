import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import mu.KotlinLogging
import org.apache.commons.io.IOUtils
import org.archive.io.warc.WARCReaderFactory
import java.awt.Desktop
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.IllegalArgumentException
import java.net.URI
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

private const val DATA_DIR = "../utopia2-data"
private const val INTERVAL = 10L
private val searchTerms = listOf("utopia", "utopie")
private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.warn("Starting Utopia Machine 2.0")

    val printer = LinePrinter()
    printer.printIntro()

    val searcher = Flowable.just(DATA_DIR)
        .flatMap { Flowable.fromIterable(listFiles(it)) }
        .map { file ->
            logger.info { "reading archive ${file.path}" }
            WARCReaderFactory.get(file.path, FileInputStream(file), true)
        }
        .flatMap { Flowable.fromIterable(it) }
        .map { record ->
            val bytes = IOUtils.toByteArray(record, record.available())
            val content = String(bytes, Charset.forName("utf-8"))
            val index = content.search()
            val snippet = if (index != -1) content.snippet(index) else null
            Pair(record, snippet)
        }
        .filter { it.second != null }
    //.repeat()

    val timer = Flowable.interval(INTERVAL, TimeUnit.SECONDS, Schedulers.computation())
        .onBackpressureDrop()

    Flowables.zip(
        timer,
        searcher,
        { tick, pair ->
            logger.debug { "tick $tick" }
            pair
        })
        .subscribeOn(Schedulers.computation())
        .subscribeBy(
            onNext = {
                val url = it.first.header?.url
                val snippet = it.second
                if (url != null && snippet != null) {
                    logger.debug(url)

                    val uri = URI(url)
                    uri.browse()
                    printer.printSnippet(uri, snippet)
                }
            },
            onComplete = { exit() },
            onError = {
                logger.error("error processing archive records", it)
                exit()
            }
        )

    waitForExit()
}

/**
 * Search this String for occurrences of [query] Strings
 * and return the index if something was found or -1 otherwise.
 */
private fun String.search(query: List<String> = searchTerms): Int {
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

/**
 * Open this URI in the desktop's web browser.
 */
private fun URI.browse() {
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(this)
    }
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

private val lock = java.lang.Object()

private fun waitForExit() {
    synchronized(lock) {
        lock.wait()
    }
}

private fun exit() {
    synchronized(lock) {
        lock.notify()
    }
}
