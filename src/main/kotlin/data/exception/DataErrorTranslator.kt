package data.exception

import domain.model.exception.DataUnavailableException
import domain.model.exception.DomainException
import domain.model.exception.OperationFailedException

fun translateDataError(error: Throwable, operation: String, entityName: String): DomainException {
    return when (error) {
        is NetworkUnavailableException, is LocalStorageUnavailableException ->
            DataUnavailableException("Data is currently unavailable while trying to $operation $entityName.")
        else ->
            OperationFailedException("Failed to $operation $entityName: ${error.message}")
    }
}
