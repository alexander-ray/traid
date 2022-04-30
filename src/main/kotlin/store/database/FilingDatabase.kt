package store.database

import store.Filing
import store.FilingType
import java.io.File
import java.text.SimpleDateFormat

/**
 * Document database implementation for filing data.
 *
 * Currently, we partition files on symbol, filing type, and filing date.
 */
class FilingDatabase(
    private val fileRoot: String
    ) {
    /**
     * Save a list of filings to the database. For some reason, we're supporting any number of partitions.
     *
     * Note we do not yet support upserts. This method will overwrite all partitions in [items].
     */
    fun save(items: List<Filing>) {
        items.forEach {
            File(it.getPath()).printWriter().use { out ->
                out.print(it.text)
            }
        }
    }

    /**
     * Load all filings for a given partition.
     *
     * TODO: should decide if the database layer guarantees sorting.
     */
    fun loadAll(type: FilingType, symbol: String): List<Filing> {
        val path = getRootPath(type, symbol)

        val file = File(path)
        if (!file.exists()) throw PartitionNotFoundException("Partition for $type and $symbol not found.")

        return file.list()
            .map { filename ->
                filename to File(filename).inputStream().bufferedReader().use { it.readText() }
            }
            .map { Filing(type, it.second , dateFromFilename(it.first), symbol) }
    }

    private fun Filing.getPath() = "$fileRoot/${this.symbol}/${this.type}/${this.date}.txt"
    private fun getRootPath(type: FilingType, symbol: String) = "$fileRoot/$symbol/$type"

    private companion object {
        private fun dateFromFilename(filename: String) =
            filename.split(".").first()
                .let { SimpleDateFormat("yyyy-MM-dd").parse(it) }
    }
}