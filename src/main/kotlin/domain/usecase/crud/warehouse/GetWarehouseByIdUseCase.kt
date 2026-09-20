package domain.usecase.crud.warehouse

import domain.exception.DatabaseConflictException
import domain.exception.ResourceNotFoundException
import domain.model.Warehouse
import domain.repository.WarehouseRepository

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository,
) {
    suspend operator fun invoke(id: String): Result<Warehouse> {

        return runCatching { warehouseRepository.getById(id) }.fold(
            onSuccess = { warehouse ->
                if (warehouse != null) {
                    Result.success(warehouse)
                } else {
                    Result.failure(ResourceNotFoundException("Warehouse with ID '$id' was not found."))
                }
            },
            onFailure = { error ->
                Result.failure(
                    DatabaseConflictException(
                        "Failed to fetch warehouse with ID '$id': ${error.message}", error
                    )
                )
            }
        )
    }
}
