import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Map<String, String>.toQueryString(): String = this.map { "${it.key}=${it.value}" }.joinToString("&")

fun buildInstantFromLocalDate(date: String, pattern: String, zone: String): Instant = LocalDate
    .parse(date, DateTimeFormatter.ofPattern(pattern))
    .atStartOfDay(ZoneId.of(zone))
    .toInstant()