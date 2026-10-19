package domain.usecase.warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class GetOverloadedWarehousesUseCase(
    private val getWarehouseLoadFactorUseCase: GetWarehouseLoadFactorUseCase,
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(): List<Warehouse> {
        return warehouseRepository.getAll()
            .filter { getWarehouseLoadFactorUseCase(it.id) > OVERLOAD_THRESHOLD }
    }

    private companion object {
        const val OVERLOAD_THRESHOLD = 1.0
    }
}
