package domain.usecase

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class GetAllWarehousesUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(): List<Warehouse>{
        return warehouseRepository.getAllWarehouses()
    }
}
