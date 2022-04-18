package utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant

class DateTimeUtilsTest {
    @Test
    fun `Instant from local date string with UTC zone`() {
        Assertions.assertEquals(
            Instant.parse("2020-01-03T00:00:00Z"),
            buildInstantFromLocalDate("2020-01-03", "yyyy-MM-dd", "UTC")
        )
    }

    @Test
    fun `Instant from local date string with ET zone`() {
        Assertions.assertEquals(
            Instant.parse("2020-01-03T05:00:00Z"),
            buildInstantFromLocalDate("2020-01-03", "yyyy-MM-dd", "US/Eastern")
        )
    }
}