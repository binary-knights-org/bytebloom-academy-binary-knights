package domain.usecase

import domain.repository.WarehouseRepository

class CalculateNetworkResilienceScoreUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(): Double {
        val warehouses = warehouseRepository.getAllWarehouses()
        if (warehouses.size <= 1) return 100.0
        val survivableBreakdowns = warehouses.count { removedWarehouse ->
            val remainingWarehouses = warehouses.filter { it.id != removedWarehouse.id }
            val isNetworkStillConnected = remainingWarehouses.all { currentWarehouse ->
                currentWarehouse.outgoingRoutes.any { route ->
                    route.destinationHub.id != removedWarehouse.id
                }
            }
            isNetworkStillConnected
        }
        return (survivableBreakdowns.toDouble() / warehouses.size) * 100.0
    }
}
