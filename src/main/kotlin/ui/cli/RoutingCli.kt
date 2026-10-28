package ui.cli

import domain.repository.WarehouseRepository
import domain.usecase.routing.FindOptimalPathUseCase

internal class RoutingCli(
    private val warehouseRepository: WarehouseRepository,
    private val findOptimalPathUseCase: FindOptimalPathUseCase,
) {
    suspend fun run() {
        runRouteCalculation(warehouseRepository, findOptimalPathUseCase)
    }
}
