package domain.usecase.crud.warehouse

import data.exception.translateDataError
import domain.model.exception.ResourceNotFoundException
import domain.model.Warehouse
import domain.repository.WarehouseRepository

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(id: String): Result<Warehouse> {
        return runCatching { warehouseRepository.getById(id) }.fold(
            onSuccess = { warehouse ->
                if (warehouse != null) {
                    Result.success(warehouse)
                } else {
                    Result.failure(ResourceNotFoundException())
                }
            },
            onFailure = { error ->
                Result.failure(translateDataError(error, "fetch", "warehouse"))
            }
        )
    }
}
