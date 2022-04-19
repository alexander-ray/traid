package store

import store.database.CsvStockTsDatabase
import store.database.Database

/**
 * Store for accessing Traid data, abstracting away most of the retrieval and cacheing.
 *
 * At the time of writing, the only data available is stock timeseries data, but eventually we might
 * add more types of data (which may or may not require different stores).
 *
 * The store uses adapters to external data sources as a mechanism for bringing data locally.
 */
class TraidStore(
    private val database: Database<StockTsDataPoint>
) {
    fun getPointsForSymbol(symbol: String): List<StockTsDataPoint> {
        return database.loadAll(symbol)
    }
}