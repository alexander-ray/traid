package utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ApiUtilsTest {
    @Test
    fun `Convert map to query string works empty map`() {
        Assertions.assertEquals("", emptyMap<String, String>().toQueryString())
    }
    @Test
    fun `Convert map to query string works single entry`() {
        Assertions.assertEquals("alex=ray", mapOf("alex" to "ray").toQueryString())
    }
    @Test
    fun `Convert map to query string works multiple entries`() {
        Assertions.assertEquals("alex=ray&another=one", mapOf("alex" to "ray", "another" to "one").toQueryString())
    }
}