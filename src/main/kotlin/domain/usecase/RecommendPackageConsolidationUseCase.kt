package domain.usecase

import domain.model.Package
import domain.model.Vehicle
import domain.model.Warehouse
import domain.repository.PackageRepository
import domain.repository.VehicleRepository

data class ConsolidationRecommendation(
    val packages: List<Package>,
    val origin: Warehouse,
    val destination: Warehouse,
    val totalWeight: Double,
    val vehicle: Vehicle
)

class RecommendPackageConsolidationUseCase(
    private val packageRepository: PackageRepository,
    private val vehicleRepository: VehicleRepository
    ) {
        operator fun invoke(): List<ConsolidationRecommendation> {
            val packages = packageRepository.getAllPackages()
            val vehicles = vehicleRepository.getAllVehicles()

            return packages.groupBy { it.originHub.id to it.destinationHub.id }
                .values.mapNotNull { packagesGroup -> createRecommendation(packagesGroup, vehicles) }
        }

        private fun createRecommendation(
            packages: List<Package>,
            vehicles: List<Vehicle>
        ): ConsolidationRecommendation? {
            if (packages.size < MIN_PACKAGES_FOR_CONSOLIDATION) {
                return null
            }

            val totalWeight = packages.sumOf { it.weight }
            val origin = packages.first().originHub
            val destination = packages.first().destinationHub

            val suitableVehicle = vehicles
                .filter { it.currentHub == origin }
                .firstOrNull { vehicle ->
                    vehicle.maxCapacityKg - vehicle.currentLoadKg >= totalWeight }

            return if (suitableVehicle != null) {
                ConsolidationRecommendation(
                    packages = packages,
                    origin = origin,
                    destination = destination,
                    totalWeight = totalWeight,
                    vehicle = suitableVehicle
                )
            } else  null
        }

        private companion object {
            const val MIN_PACKAGES_FOR_CONSOLIDATION = 2
        }
    }
