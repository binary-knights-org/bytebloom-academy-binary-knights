package data.exception

sealed class DataException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    companion object {
        const val NETWORK_UNAVAILABLE = "Network connection is currently unavailable."
        const val DATABASE_CONFLICT = "Database conflict occurred."
        const val LOCAL_STORAGE_UNAVAILABLE = "Local storage is currently unavailable."

        const val NETWORK_UNREACHABLE_FETCH = "Network unreachable - Falling back to CSV"
        const val NETWORK_IO_ERROR_FETCH = "IO Network Error - Falling back to CSV"

        const val NETWORK_UNREACHABLE_CREATE = "Network unreachable during create."
        const val NETWORK_IO_ERROR_CREATE = "IO network error during create."

        const val NETWORK_UNREACHABLE_UPDATE = "Network unreachable during update."
        const val NETWORK_IO_ERROR_UPDATE = "IO network error during update."

        const val NETWORK_UNREACHABLE_DELETE = "Network unreachable during delete."
        const val NETWORK_IO_ERROR_DELETE = "IO network error during delete."

        const val CSV_FILE_NOT_FOUND = "CSV file not found at path:"
        const val CSV_FILE_CREATION_FAILED = "Failed to create CSV file at path:"
        const val CSV_READ_ERROR = "An error occurred while reading the CSV file."

        const val INVALID_BUCKET_CAPACITY = "maxBucketCapacity must be greater than 0."
        const val INVALID_REFILL_RATE = "refillRatePerSecond must be a positive finite number."
        const val CALL_NOT_PERMITTED = "Circuit breaker is OPEN. Call is not permitted."
    }
}

class NetworkUnavailableException(message: String = NETWORK_UNAVAILABLE) :
    DataException(message)
class DatabaseConflictException(message: String = DATABASE_CONFLICT) :
    DataException(message)
class LocalStorageUnavailableException(message: String = LOCAL_STORAGE_UNAVAILABLE) :
    DataException(message)
class CsvFileNotFoundException(filePath: String, cause: Throwable? = null) :
    DataException("$CSV_FILE_NOT_FOUND $filePath", cause)
class CsvFileCreationException(filePath: String, cause: Throwable? = null) :
    DataException("$CSV_FILE_CREATION_FAILED $filePath", cause)
class CsvReadException(cause: Throwable? = null) :
    DataException(CSV_READ_ERROR, cause)
class InvalidBucketCapacityException(message: String = INVALID_BUCKET_CAPACITY) :
    DataException(message)
class InvalidRefillRateException(message: String = INVALID_REFILL_RATE) :
    DataException(message)
class CallNotPermittedException(message: String = CALL_NOT_PERMITTED) :
    DataException(message)

