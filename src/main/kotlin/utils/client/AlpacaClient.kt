package utils.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.apache.logging.log4j.LogManager

/**
 * Lightweight, low-level wrapper class for the Alpaca API. Currently returns bare JsonObject,
 * to be used by higher-level components to marshall the JSON into something useful.
 *
 * Currently only uses the "paper" API.
 */
class AlpacaClient(private val client: HttpClient, private val mapper: ObjectMapper) {
    suspend fun makeOrder(
        symbol: String,
        quantity: Int,
        side: AlpacaOrderSide,
        type: AlpacaOrderType,
        timeInForce: AlpacaTimeInForce
    ): JsonNode {
        val url = "$ALPHA_PAPER_HOST/v2/orders"

        val body = mapOf(
            "symbol" to symbol,
            "qty" to quantity.toString(),
            "side" to side.name,
            "type" to type.name,
            "time_in_force" to timeInForce.name
        )

        println("Making request to $url")
        log.debug("Making request to $url")

        val result = client.post(url) {
            headers {
                append(ALPACA_API_KEY_HEADER, getAlpacaPaperApiKey())
                append(ALPACA_API_KEY_SECRET, getAlpacaPaperApiSecret())
            }
            setBody(mapper.writeValueAsString(body))
        }

        return mapper.readTree(result.bodyAsText())
    }

    private companion object {
        private val log = LogManager.getLogger()

        private const val ALPHA_PAPER_HOST = "https://paper-api.alpaca.markets"

        private const val ALPACA_PAPER_API_KEY_ENV_VARIABLE = "ALPACA_PAPER_API_KEY"
        private const val ALPACA_PAPER_API_SECRET_ENV_VARIABLE = "ALPACA_PAPER_API_SECRET"

        private const val ALPACA_API_KEY_HEADER = "APCA-API-KEY-ID"
        private const val ALPACA_API_KEY_SECRET = "APCA-API-SECRET-KEY"

        private fun getAlpacaPaperApiKey() = System.getenv(ALPACA_PAPER_API_KEY_ENV_VARIABLE)
            ?: error("Could not locate environment variable $ALPACA_PAPER_API_KEY_ENV_VARIABLE.")
        private fun getAlpacaPaperApiSecret() = System.getenv(ALPACA_PAPER_API_SECRET_ENV_VARIABLE)
            ?: error("Could not locate environment variable $ALPACA_PAPER_API_SECRET_ENV_VARIABLE.")
    }
}

enum class AlpacaOrderSide {
    buy, sell;
}

enum class AlpacaOrderType {
    market
}

enum class AlpacaTimeInForce {
    day
}