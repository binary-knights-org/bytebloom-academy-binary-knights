package domain.usecase.crud.warehouse

import domain.model.Warehouse
import domain.model.exception.ResourceNotFoundException
import domain.repository.WarehouseRepository

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(id: String): Result<Warehouse> =
        runCatching {
            warehouseRepository.getById(id)
                ?: throw ResourceNotFoundException()
        }
}
