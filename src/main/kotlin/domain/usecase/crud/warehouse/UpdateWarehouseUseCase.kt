package domain.usecase.crud.warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(warehouse: Warehouse): Boolean {
       return warehouseRepository.updateWarehouse(warehouse)
    }
}
