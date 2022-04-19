package store.adapter.alphavantage

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.apache.logging.log4j.LogManager
import store.StockTsDataPoint
import utils.buildInstantFromLocalDate
import utils.toQueryString
import utils.writeCsvFile

/**
 * Simple client class for the Alpha Vantage API.
 *
 * Currently, the API takes and returns normal java types and types declared in AlphaModel.
 *
 * TODO: make more general "get data" client that supports csv cacheing
 *
 * TODO: we should be updating the Alpha models as the scope of this class shrinks
 */
class AlphaClient(
    private val client: HttpClient,
    private val mapper: ObjectMapper) {
    suspend fun getDailyTimeSeries(symbol: String, compact: Boolean = true): List<StockTsDataPoint> {
        val result = client.getAlpha(
            function = AlphaFunction.TIME_SERIES_DAILY,
            symbol = symbol,
            datatype = AlphaFileFormat.JSON,
            outputSize = if (compact) AlphaOutputSize.COMPACT else AlphaOutputSize.FULL
        )
        val alphaTs = result.bodyAsText().toAlphaTimeSeries(AlphaFunction.TIME_SERIES_DAILY)
        return alphaTs.toStockTsDataPointList(symbol)
    }

    private fun String.toAlphaTimeSeries(function: AlphaFunction): AlphaTimeSeries {
        val json = mapper.readTree(this)

        val key = FUNCTION_KEY_MAP[function]
        check(json.has(key)) { "Key $key does not exist in json." }

        return AlphaTimeSeries(
            grain = FUNCTION_GRAIN_MAP[function]!!,
            series = json[FUNCTION_KEY_MAP[function]].fields().asSequence().map { (key, value) ->
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

        private enum class AlphaFunction {
            TIME_SERIES_DAILY,
            OVERVIEW
        }

        private enum class AlphaFileFormat(val str: String) {
            JSON("json"), CSV("csv");

            override fun toString(): String = str
        }

        private enum class AlphaOutputSize(val str: String) {
            FULL("full"), COMPACT("compact");

            override fun toString(): String = str
        }

        private suspend fun HttpClient.getAlpha(
            function: AlphaFunction = AlphaFunction.TIME_SERIES_DAILY,
            symbol: String = "AMZN",
            datatype: AlphaFileFormat = AlphaFileFormat.JSON,
            outputSize: AlphaOutputSize = AlphaOutputSize.COMPACT,
        ): HttpResponse {
            val params: Map<String, String> = mapOf(
                "function" to function.toString(),
                "symbol" to symbol,
                "datatype" to datatype.toString(),
                "outputsize" to outputSize.toString(),
                "apikey" to getAlphaVantageApiKey()
            )
            val url = "https://www.alphavantage.co/query?${params.toQueryString()}"
            log.debug("Making request to $url")
            return this.get(url)
        }

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