package store.adapter.alphavantage

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import io.ktor.client.*
import io.ktor.client.statement.*
import org.apache.logging.log4j.LogManager
import store.StockTsDataPoint
import utils.buildInstantFromLocalDate
import utils.client.AlphaFileFormat
import utils.client.AlphaFunction
import utils.client.AlphaOutputSize
import utils.client.AlphaVantageClient
import java.math.BigDecimal
import java.time.Instant

/**
 * Simple client class for the Alpha Vantage API.
 *
 * Currently, the API takes and returns normal java types and types declared in AlphaModel.
 *
 * TODO: we should be updating the Alpha models as the scope of this class shrinks
 */
class AlphaVantageAdapter(
    private val client: AlphaVantageClient,
    private val mapper: ObjectMapper) {

    /**
     * TODO add error handling for bad symbol
     */
    suspend fun getDailyTimeSeries(symbol: String, compact: Boolean = true): List<StockTsDataPoint> {
        val result = client.getAlphaDailyTimeSeries(
            function = AlphaFunction.TIME_SERIES_DAILY,
            symbol = symbol,
            datatype = AlphaFileFormat.JSON,
            outputSize = if (compact) AlphaOutputSize.COMPACT else AlphaOutputSize.FULL
        )
        val alphaTs = result.toAlphaTimeSeries(AlphaFunction.TIME_SERIES_DAILY)
        return alphaTs.toStockTsDataPointList(symbol)
    }

    private fun JsonNode.toAlphaTimeSeries(function: AlphaFunction): AlphaTimeSeries {
        val key = FUNCTION_KEY_MAP[function]
        check(this.has(key)) { "Key $key does not exist in json." }

        return AlphaTimeSeries(
            grain = FUNCTION_GRAIN_MAP[function]!!,
            series = this[FUNCTION_KEY_MAP[function]].fields().asSequence().map { (key, value) ->
                val instant = buildInstantFromLocalDate(key, "yyyy-MM-dd", "US/Eastern")
                // This should really be done with a custom deserializer
                // But I don't yet know if the numbering/field names are always consistent across APIs
                val pointMap = mapper.convertValue<Map<String, Object>>(value).mapKeys { it.key.cleanIfNumberedKey() }
                val point = mapper.convertValue<AlphaDataPoint>(pointMap)
                Pair(instant, point)
            }.toList()
        )
    }

    private companion object {
        private val log = LogManager.getLogger()

        private val FUNCTION_KEY_MAP: Map<AlphaFunction, String> = mapOf(
            AlphaFunction.TIME_SERIES_DAILY to "Time Series (Daily)"
        )
        private val FUNCTION_GRAIN_MAP: Map<AlphaFunction, AlphaTimeGrain> = mapOf(
            AlphaFunction.TIME_SERIES_DAILY to AlphaTimeGrain.DAILY
        )

        private const val ALPHA_VANTAGE_API_ENV_VARIABLE = "ALPHA_VANTAGE_API_KEY"
        private fun getAlphaVantageApiKey() = System.getenv(ALPHA_VANTAGE_API_ENV_VARIABLE)
            ?: error("Could not locate environment variable $ALPHA_VANTAGE_API_ENV_VARIABLE.")

        private val NUMBERED_KEY_REGEX = Regex("\\d\\. [A-Za-z]+\$")
        private fun String.cleanIfNumberedKey(): String = if (NUMBERED_KEY_REGEX.matches(this)) this.split(" ").last() else this

        private fun AlphaTimeSeries.toStockTsDataPointList(symbol: String): List<StockTsDataPoint> = this.series.map { (dt, alphaDp) ->
            StockTsDataPoint(
                datetime = dt,
                open = alphaDp.open,
                close = alphaDp.close,
                low = alphaDp.low,
                high = alphaDp.high,
                volume = alphaDp.volume,
                symbol = symbol
            )
        }
    }
}

/**
 * TODO: remove these models and go straight from JsonObject to the external data models
 */
private enum class AlphaTimeGrain {
    DAILY
}

private data class AlphaDataPoint(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long
)

// Might be better if we went with a sequence?
private data class AlphaTimeSeries(
    val grain: AlphaTimeGrain,
    val series: List<Pair<Instant, AlphaDataPoint>>
) {
    // This probably means yes?
    fun sortedTimeSeries(): List<Pair<Instant, AlphaDataPoint>> = series.sortedBy { it.first }
}