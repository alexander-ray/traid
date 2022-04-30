package store.database

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import store.StockTsDataPoint
import utils.readCsvFile
import utils.writeCsvFile
import java.io.File

/**
 * CSV-based database implementation for stock timeseries data.
 *
 * Currently, we partition files only on symbol and not time. The ticking time bomb is
 * going to be the fact that we're not yet accounting for grain and assume everything is daily.
 */
class CsvStockTsDatabase(
    private val fileRoot: String,
    private val csvMapper: CsvMapper
    ) {
    /**
     * Save a list of datapoints to the database. For some reason, we're supporting any number of partitions.
     *
     * Note we do not yet support upserts. This method will overwrite all partitions in [items].
     */
    fun save(items: List<StockTsDataPoint>) {
        items
            .groupBy { it.symbol }
            .forEach { (symbol, points) ->
                val sortedPoints = points.sortedBy { it.datetime }
                csvMapper.writeCsvFile(sortedPoints, getPathForPartition(symbol))
            }
    }

    /**
     * Load all points for a given partition.
     *
     * TODO: should decide if the database layer guarantees sorting.
     */
    fun loadAll(symbol: String): List<StockTsDataPoint> {
        val path = getPathForPartition(symbol)

        if (!File(path).exists()) throw PartitionNotFoundException("Partition $symbol not found.")

        return csvMapper.readCsvFile(path)
    }

    private fun getPathForPartition(partition: String) = "$fileRoot/$partition.csv"
}