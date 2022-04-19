package scenarios

import store.adapter.alphavantage.AlphaTimeSeries
import java.math.BigDecimal
import java.time.Instant

/**
 * This whole file will certainly need to be ripped out when we know what we want. More of a scratch file than anything.
 */

/**
 * Returns initial and final price.
 */
fun replayOneTimeTrade(ts: AlphaTimeSeries, shares: Double, startDate: Instant, endDate: Instant): Pair<BigDecimal, BigDecimal> {
    val series = ts.sortedTimeSeries()
        .filter { it.first.isAfter(startDate) && it.first.isBefore(endDate) }

    val initial = series.first().second.open.times(BigDecimal.valueOf(shares))
    val final = series.last().second.close.times(BigDecimal.valueOf(shares))

    return Pair(initial, final)
}