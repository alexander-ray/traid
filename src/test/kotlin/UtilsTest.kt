import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class UtilsTest {
    @Test
    fun `Convert map to query string works empty map`() {
        assertEquals("", emptyMap<String, String>().toQueryString())
    }
    @Test
    fun `Convert map to query string works single entry`() {
        assertEquals("alex=ray", mapOf("alex" to "ray").toQueryString())
    }
    @Test
    fun `Convert map to query string works multiple entries`() {
        assertEquals("alex=ray&another=one", mapOf("alex" to "ray", "another" to "one").toQueryString())
    }

    @Test
    fun `Instant from local date string with UTC zone`() {
        assertEquals(
            Instant.parse("2020-01-03T00:00:00Z"),
            buildInstantFromLocalDate("2020-01-03", "yyyy-MM-dd", "UTC")
        )
    }

    @Test
    fun `Instant from local date string with ET zone`() {
        assertEquals(
            Instant.parse("2020-01-03T05:00:00Z"),
            buildInstantFromLocalDate("2020-01-03", "yyyy-MM-dd", "US/Eastern")
        )
    }
}