package domain.usecase.crud.warehouse

import domain.model.exception.OperationFailedException
import data.exception.translateDataError
import domain.repository.WarehouseRepository

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return runCatching { warehouseRepository.delete(id) }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) {
                    Result.success(Unit)
                } else {
                    Result.failure(OperationFailedException())
                }
            },
            onFailure = { error ->
                Result.failure(translateDataError(error, "delete", "warehouse"))
            }
        )
    }
}
