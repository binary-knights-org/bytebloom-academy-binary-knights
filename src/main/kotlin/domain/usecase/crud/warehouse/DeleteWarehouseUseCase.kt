package domain.usecase.crud.warehouse

import domain.exception.DatabaseConflictException
import domain.repository.WarehouseRepository

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> {

        return runCatching { warehouseRepository.delete(id) }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) {
                    Result.success(Unit)
                } else {
                    Result.failure(DatabaseConflictException("Failed to delete warehouse with ID '$id' from database."))
                }
            },
            onFailure = { error ->
                Result.failure(DatabaseConflictException("Failed to delete warehouse: ${error.message}", error))
            }
        )
    }
}
