package store.database

/**
 * Generic interface for a database adapter.
 */
interface Database<T> {
    fun save(items: List<T>)

    fun loadAll(partition: String): List<T>
}