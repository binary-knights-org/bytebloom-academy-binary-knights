package domain.usecase.crud.Warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(id: String) : Warehouse?{
        return warehouseRepository.getWarehouseById(id)
    }
}
