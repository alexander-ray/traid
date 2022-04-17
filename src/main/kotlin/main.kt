import alpha.AlphaClient
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import scenarios.replayOneTimeTrade
import java.time.Instant

suspend fun main() {
    val client = HttpClient(CIO)
    val alphaClient = AlphaClient(client, jacksonObjectMapper())

    val ts = alphaClient.getDailyTimeSeries(
        symbol = "AMZN",
        compact = false
    )

    println(replayOneTimeTrade(ts, 1.0, Instant.parse("2001-05-01T00:00:00Z"), Instant.now()))

    client.close()
}