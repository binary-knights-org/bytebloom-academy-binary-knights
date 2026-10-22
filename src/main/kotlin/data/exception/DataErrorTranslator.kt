package data.exception

import domain.model.exception.DataUnavailableException
import domain.model.exception.DomainException
import domain.model.exception.OperationFailedException
import kotlinx.coroutines.CancellationException

fun translateDataError(
    error: Throwable,
    operation: String,
    entityName: String
): DomainException {
    if (error is CancellationException) {
        throw error
    }

    return when (error) {
        is NetworkUnavailableException,
        is CallNotPermittedException,
        is LocalStorageUnavailableException ->
            DataUnavailableException(
                "Data is currently unavailable while trying to $operation $entityName."
            )

        else ->
            OperationFailedException(
                "Failed to $operation $entityName: ${error.message}"
            )
    }
}
