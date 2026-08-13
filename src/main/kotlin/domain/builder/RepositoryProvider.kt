package domain.builder

import domain.repository.FleetRepository
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository

data class RepositoryProvider(
    val fleetRepository: FleetRepository,
    val warehouseRepository: WarehouseRepository,
    val packageRepository: PackageRepository,
    val routeRepository: RouteRepository
)
