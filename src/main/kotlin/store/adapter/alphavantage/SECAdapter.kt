package store.adapter.alphavantage

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
import java.math.BigDecimal
import java.time.Instant

/**
 * Simple client class for the Alpha Vantage API.
 *
 * Currently, the API takes and returns normal java types and types declared in AlphaModel.
 *
 * TODO: we should be updating the Alpha models as the scope of this class shrinks
 */
class SECAdapter(
    private val client: HttpClient,
    private val mapper: ObjectMapper) {

    /**
     * TODO add error handling for bad symbol
     */
//    suspend fun getSECFilings(symbol: String, filingType: String): List<String> {
//
//    }

    private companion object {
        private val log = LogManager.getLogger()
    }
}