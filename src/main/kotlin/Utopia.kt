import mu.KotlinLogging
import org.archive.io.warc.WARCReaderFactory
import java.io.FileInputStream

private const val FILE = "data/CC-MAIN-20131204131715-00000-ip-10-33-133-15.ec2.internal.warc.wet.gz"
private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.warn("Starting Utopia Machine")

    val fis = FileInputStream(FILE)
    val archiveReader = WARCReaderFactory.get(FILE, fis, true)

    for (record in archiveReader) {
        val header = record.header
        logger.debug(header.url)
    }

    logger.warn("Utopia Machine stopped")
}
