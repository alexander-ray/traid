package utils.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.apache.logging.log4j.LogManager
import utils.buildInstantFromLocalDate
import java.time.Instant

private val log = LogManager.getLogger()

class SECClient(
    private val client: HttpClient,
    // Currently necessary for mapping symbol to CIK. :shrug:
    private val alphaVantageClient: AlphaVantageClient,
    private val mapper: ObjectMapper
    ) {

    suspend fun getRawSecFilings(symbol: String = "AMZN", filingType: String = "10-K"): List<String> {
        val cik = alphaVantageClient.symbolToCik(symbol)
        val submissionJson = getSecSubmissions(cik)

        return compileDocumentInfo(submissionJson)
            .filter { it.filingType == filingType }
            .sortedByDescending { it.filingDate }
            .map { getSecFiling(cik, it.accessionNumber, it.primaryDocument) }
    }

    private suspend fun getSecFiling(cik: String, accessionNumber: String, primaryDocument: String): String {
        val url = "https://www.sec.gov/Archives/edgar/data/${cleanCik(cik)}/${accessionNumber.replace("-", "")}/$primaryDocument"

        val response = client.get(url) {
            headers {
                append(HttpHeaders.AcceptEncoding, "gzip, deflate")
                append(HttpHeaders.UserAgent, "SECFilingAnalysisProject 1alexray@gmail.com")
            }
        }

        return response.bodyAsText()
    }

    // Honestly, this isn't pleasant
    private fun compileDocumentInfo(submissionJson: JsonNode): List<DocumentInfo> {
        val core = submissionJson["filings"]["recent"] // Is there more in the "files" file? Not sure. This seems limited to the past ~8 years
        val filingDates = core["filingDate"]
        val formTypes = core["form"]
        val primaryDocuments = core["primaryDocument"]

        return core["accessionNumber"].asIterable().withIndex().map {
            val idx = it.index
            val accessionNumber = it.value

            DocumentInfo(
                // Not actually sure about TZ, ET should be fine
                buildInstantFromLocalDate(filingDates[idx].asText(), "yyyy-MM-dd", "US/Eastern"),
                formTypes[idx].asText(),
                accessionNumber.asText(),
                primaryDocuments[idx].asText()
            )
        }
    }

    private suspend fun getSecSubmissions(
        cik: String = "0001018724" // Amazon
    ): JsonNode {
        val url = "https://data.sec.gov/submissions/CIK${cleanCik(cik)}.json"

        log.debug("Making request to $url")
        val response = client.get(url) {
            headers {
                append(HttpHeaders.AcceptEncoding, "gzip, deflate")
                append(HttpHeaders.UserAgent, "SECFilingAnalysisProject 1alexray@gmail.com")
            }
        }

        return mapper.readTree(response.bodyAsText())
    }

    private companion object {
        private data class DocumentInfo(
            val filingDate: Instant, // Maybe should change to Date
            val filingType: String,
            val accessionNumber: String,
            val primaryDocument: String
        )

        private fun cleanCik(cik: String): String {
            require(cik.length in 1..10) { "CIK length must be between 1 and 10, found ${cik.length}." }
            // Alpha vantage API, for example, doesn't return the padded version
            return cik.padStart(10, '0')
        }
    }
}