package store.adapter.alphavantage

import java.math.BigDecimal
import java.time.Instant


enum class AlphaTimeGrain {
    DAILY
}

data class AlphaDataPoint(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long
)

// Might be better if we went with a sequence?
data class AlphaTimeSeries(
    val grain: AlphaTimeGrain,
    val series: List<Pair<Instant, AlphaDataPoint>>
) {
    // This probably means yes?
    fun sortedTimeSeries(): List<Pair<Instant, AlphaDataPoint>> = series.sortedBy { it.first }
}