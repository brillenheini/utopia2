import mu.KotlinLogging
import org.apache.commons.io.IOUtils
import org.archive.io.warc.WARCReaderFactory
import java.io.FileInputStream
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
        if (content.contains("utopia")) {
            logger.debug { header.url }
        }
    }

    logger.warn("Utopia Machine stopped")
}
