package store

import java.util.Date

/**
 * General data class representing a regulatory filing.
 */
data class Filing(
    val type: FilingType,
    val text: String,
    val date: Date,
    val symbol: String
)

enum class FilingType {
    SEC10K
}