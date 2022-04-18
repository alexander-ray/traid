package utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun buildInstantFromLocalDate(date: String, pattern: String, zone: String): Instant = LocalDate
    .parse(date, DateTimeFormatter.ofPattern(pattern))
    .atStartOfDay(ZoneId.of(zone))
    .toInstant()