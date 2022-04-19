package store

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import store.adapter.alphavantage.AlphaVantageAdapter
import store.database.Database
import store.database.PartitionNotFoundException
import java.time.Duration
import java.time.Instant

/**
 * Store for accessing Traid data, abstracting away most of the retrieval and cacheing.
 *
 * At the time of writing, the only data available is stock timeseries data, but eventually we might
 * add more types of data (which may or may not require different stores).
 *
 * The store uses adapters to external data sources as a mechanism for bringing data locally.
 */
class TraidStore(
    private val database: Database<StockTsDataPoint>,
    // TODO: this should ideally be more generic, but I haven't gotten there yet. Will probably regret later.
    private val alphaVantageAdapter: AlphaVantageAdapter
    ) {
    fun getPointsForSymbol(symbol: String): List<StockTsDataPoint> {
        val points = try {
            database.loadAll(symbol)
        } catch (e: PartitionNotFoundException) {
            log.debug("No existing partition found in DB for symbol $symbol.")
            emptyList()
        }

        return if (points.isEmpty() || !points.last().datetime.isCacheHit()) {
            // TODO: this api should probably just be suspend
            runBlocking {
                database.save(alphaVantageAdapter.getDailyTimeSeries(symbol, compact = false))
            }
            database.loadAll(symbol)
        } else {
            points
        }
    }

    private companion object {
        private val log = LogManager.getLogger()

        private val CACHE_LAG = Duration.ofDays(2)

        private fun Instant.isCacheHit() = this.isAfter(Instant.now().minus(CACHE_LAG))
    }
}