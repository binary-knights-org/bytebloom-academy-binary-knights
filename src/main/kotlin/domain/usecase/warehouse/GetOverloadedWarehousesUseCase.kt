package domain.usecase.warehouse

import domain.model.Warehouse
import domain.repository.WarehouseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetOverloadedWarehousesUseCase(
    private val getWarehouseLoadFactorUseCase: GetWarehouseLoadFactorUseCase,
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(): List<Warehouse> = coroutineScope {
        warehouseRepository.getAll()
            .map { warehouse ->
                warehouse to async { getWarehouseLoadFactorUseCase(warehouse.id) }
            }
            .filter { (_, loadFactor) -> loadFactor.await() > OVERLOAD_THRESHOLD }
            .map { (warehouse, _) -> warehouse }
    }
    private companion object {
        const val OVERLOAD_THRESHOLD = 1.0
    }
}
