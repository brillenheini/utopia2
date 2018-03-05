import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import mu.KotlinLogging
import java.awt.Desktop
import java.io.File
import java.io.FileNotFoundException
import java.net.URI
import java.util.concurrent.TimeUnit

private const val DEBUG = false
private const val DATA_DIR = "../utopia2-data"
private const val INTERVAL = 60L
private val searchTerms = listOf("utopia", "utopie")
private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.warn("Starting Utopia Machine 2.0")

    val printer = LinePrinter(DEBUG)
    printer.printIntro()

    val searcher = Flowable.merge(
        listFiles(DATA_DIR).map { createArchiveSearcher(it, searchTerms) }
    )

    val timer = Flowable.interval(INTERVAL, TimeUnit.SECONDS, Schedulers.computation())
        .onBackpressureDrop()

    Flowables.zip(
        timer,
        searcher,
        { tick, pair ->
            logger.trace { "tick $tick" }
            pair
        })
        .repeat()
        .subscribeOn(Schedulers.computation())
        .subscribeBy(
            onNext = { (url, snippet) ->
                if (url != null && snippet != null) {
                    logger.debug(url)
                    val uri = URI(url)
                    uri.browse()
                    printer.printSnippet(uri, snippet)
                }
            },
            onComplete = { exit() },
            onError = {
                logger.error("error processing archives", it)
                exit()
            }
        )

    waitForExit()
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

/**
 * Open this URI in the desktop's web browser.
 */
private fun URI.browse() {
    if (!DEBUG && Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(this)
    }
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
