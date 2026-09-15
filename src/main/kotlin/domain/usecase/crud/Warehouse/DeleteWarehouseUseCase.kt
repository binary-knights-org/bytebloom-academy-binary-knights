package domain.usecase.crud.Warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(id: String) : Boolean{
        return warehouseRepository.deleteWarehouse(id)
    }
}

