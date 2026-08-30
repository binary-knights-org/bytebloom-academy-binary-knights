package domain.usecase

import domain.repository.WarehouseRepository

private const val MINIMUM_NETWORK_SIZE = 1
private const val PERFECT_RESILIENCE_SCORE = 100.0

class CalculateNetworkResilienceScoreUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(): Double {
        val warehouses = warehouseRepository.getAllWarehouses()
        if (warehouses.size <= MINIMUM_NETWORK_SIZE) return PERFECT_RESILIENCE_SCORE
        val survivableBreakdowns = warehouses.count { removedWarehouse ->
            val remainingWarehouses = warehouses.filter { it.id != removedWarehouse.id }
            val isNetworkStillConnected = remainingWarehouses.all { currentWarehouse ->
                currentWarehouse.outgoingRoutes.any { route ->
                    route.destinationHub.id != removedWarehouse.id
                }
            }
            isNetworkStillConnected
        }
        return (survivableBreakdowns.toDouble() / warehouses.size) * PERFECT_RESILIENCE_SCORE
    }
}
