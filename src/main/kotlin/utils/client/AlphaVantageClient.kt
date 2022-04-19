package utils.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.apache.logging.log4j.LogManager
import store.adapter.alphavantage.AlphaVantageAdapter
import utils.toQueryString

/**
 * Lightweight, low-level wrapper class for the AlphaVantage API. Currently returns bare JsonObject,
 * to be used by higher-level components to marshall the JSON into something useful.
 */
class AlphaVantageClient(private val client: HttpClient, private val mapper: ObjectMapper) {
    suspend fun getAlpha(
        params: Map<String, String>,
        function: AlphaFunction? = null,
        symbol: String? = null
    ): JsonNode {
        val queryParams: MutableMap<String, String> = mutableMapOf()
        params.forEach {
            queryParams[it.key] = it.value
        }
        if (function != null) queryParams["function"] = function.toString()
        if (symbol != null) queryParams["symbol"] = symbol.toString()

        queryParams["apikey"] = getAlphaVantageApiKey()

        val url = "https://www.alphavantage.co/query?${queryParams.toQueryString()}"
        println("Making request to $url")
        log.debug("Making request to $url")

        val result = client.get(url)

        return mapper.readTree(result.bodyAsText())
    }

    suspend fun getAlphaDailyTimeSeries(
        function: AlphaFunction = AlphaFunction.TIME_SERIES_DAILY,
        symbol: String = "AMZN",
        datatype: AlphaFileFormat = AlphaFileFormat.JSON,
        outputSize: AlphaOutputSize = AlphaOutputSize.COMPACT,
    ): JsonNode {
        return this.getAlpha(
            params = mapOf(
                "datatype" to datatype.toString(),
                "outputsize" to outputSize.toString()
            ),
            function = function,
            symbol = symbol
        )
    }

    suspend fun symbolToCik(symbol: String = "AMZN"): String {
        return this.getAlpha(emptyMap(), function = AlphaFunction.OVERVIEW, symbol = symbol).get("CIK").asText()
    }

    private companion object {
        private val log = LogManager.getLogger()

        private const val ALPHA_VANTAGE_API_ENV_VARIABLE = "ALPHA_VANTAGE_API_KEY"
        private fun getAlphaVantageApiKey() = System.getenv(ALPHA_VANTAGE_API_ENV_VARIABLE)
            ?: error("Could not locate environment variable $ALPHA_VANTAGE_API_ENV_VARIABLE.")
    }
}

enum class AlphaFunction {
    TIME_SERIES_DAILY,
    OVERVIEW
}

enum class AlphaFileFormat(val str: String) {
    JSON("json"), CSV("csv");

    override fun toString(): String = str
}

enum class AlphaOutputSize(val str: String) {
    FULL("full"), COMPACT("compact");

    override fun toString(): String = str
}
