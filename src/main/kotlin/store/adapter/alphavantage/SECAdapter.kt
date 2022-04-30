package store.adapter.alphavantage

import org.apache.logging.log4j.LogManager
import utils.client.SECClient

/**
 * Simple client class for the Alpha Vantage API.
 *
 * Currently, the API takes and returns normal java types and types declared in AlphaModel.
 *
 * TODO: we should be updating the Alpha models as the scope of this class shrinks
 */
class SECAdapter(
    private val client: SECClient) {

    /**
     * TODO add error handling for bad symbol
     */
    suspend fun getSECFilings(symbol: String, filingType: String): List<String> = client.getRawSecFilings()

    private companion object {
        private val log = LogManager.getLogger()
    }
}