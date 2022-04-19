package store.database

class PartitionNotFoundException(override val message: String?): RuntimeException(message)