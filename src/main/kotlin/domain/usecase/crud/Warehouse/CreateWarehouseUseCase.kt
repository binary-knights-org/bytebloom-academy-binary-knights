package domain.usecase.crud.Warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(warehouse: Warehouse) : Boolean{
      return warehouseRepository.createWarehouse(warehouse)
    }
}
