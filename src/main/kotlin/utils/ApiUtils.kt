package utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.statement.*

private val mapper = ObjectMapper()

fun Map<String, String>.toQueryString(): String = this.map { "${it.key}=${it.value}" }.joinToString("&")

suspend fun HttpResponse.bodyAsJson(): JsonNode = mapper.readTree(this.bodyAsText())
