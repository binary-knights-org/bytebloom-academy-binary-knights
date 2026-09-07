package domain.usecase.warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository

private const val OVERLOAD_THRESHOLD = 1.0

class GetOverloadedWarehousesUseCase(
    private val getWarehouseLoadFactorUseCase: GetWarehouseLoadFactorUseCase,
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(): List<Warehouse> {
        return warehouseRepository.getAllWarehouses()
            .filter { getWarehouseLoadFactorUseCase(it.id) > OVERLOAD_THRESHOLD }
    }
}
