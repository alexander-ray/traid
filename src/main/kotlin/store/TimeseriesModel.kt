package store

import java.math.BigDecimal
import java.time.Instant

/**
 * General data class representing data about a single stock at a given point in time.
 *
 * Note that there is a single datetime here and not a [begin, end] range; by convention this is the
 * beginning of the time range, and as such a series of [StockTsDataPoint] is not interpretable without
 * external information on the grain (or a sorted list of datapoints). We may decide to change this in the future.
 */
data class StockTsDataPoint(
    val datetime: Instant,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long,
    val symbol: String,
)