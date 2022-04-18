import alpha.AlphaClient
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import scenarios.replayOneTimeTrade
import java.time.Instant

suspend fun main() {
    val client = HttpClient(CIO)
    val csvMapper = CsvMapper().apply {
        registerModule(KotlinModule.Builder().build())
        registerModule(JavaTimeModule())
    }
    val alphaClient = AlphaClient(client, jacksonObjectMapper(), csvMapper)

    alphaClient.saveDailyTimeSeries(
        symbol = "BAX",
        filePath = "tmp.csv",
        compact = true
    )

    //println(replayOneTimeTrade(ts, 1.0, Instant.parse("2001-05-01T00:00:00Z"), Instant.now()))

    client.close()
}