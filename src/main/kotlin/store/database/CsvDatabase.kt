package store.database

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import store.StockTsDataPoint
import utils.writeCsvFile

/**
 * CSV-based database implementation for stock timeseries data.
 *
 * Currently, we partition files only on symbol and not time. The ticking time bomb is
 * going to be the fact that we're not yet accounting for grain and assume everything is daily.
 */
class CsvStockTsDatabase(
    private val fileRoot: String,
    private val csvMapper: CsvMapper
    ): Database<StockTsDataPoint> {
    /**
     * Note we do not yet support upserts. This method will overwrite all partitions in [items].
     */
    override fun save(items: List<StockTsDataPoint>) {
        items
            .groupBy { it.symbol }
            .forEach { (symbol, points) ->
                csvMapper.writeCsvFile(items, "$fileRoot/$symbol.csv")
            }
    }

    override fun loadAll(partition: String): List<StockTsDataPoint> {
        TODO("Not yet implemented")
    }


}
