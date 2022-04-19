import store.adapter.alphavantage.AlphaVantageAdapter
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import store.TraidStore
import store.database.CsvStockTsDatabase

suspend fun main() {
    val client = HttpClient(CIO)
    val csvMapper = CsvMapper().apply {
        registerModule(KotlinModule.Builder().build())
        registerModule(JavaTimeModule())
    }
    val alphaClient = AlphaVantageAdapter(client, jacksonObjectMapper())
    val csvStockTsDatabase = CsvStockTsDatabase("./", csvMapper)

    val store = TraidStore(csvStockTsDatabase, alphaClient)

    println(store.getPointsForSymbol("AMZN"))

    client.close()
}