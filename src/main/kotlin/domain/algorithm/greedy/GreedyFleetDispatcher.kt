package domain.algorithm.greedy

import domain.model.RegionalZone
import domain.model.assignment.VehicleCoverage

class GreedyFleetDispatcher {

    fun dispatch(
        targetZones: Set<RegionalZone>,
        vehicleCoverages: List<VehicleCoverage>
    ): List<VehicleCoverage> {

        val uncoveredZones = targetZones.toMutableSet()
        val selectedVehicles = mutableListOf<VehicleCoverage>()

        while (uncoveredZones.isNotEmpty()) {

            val bestVehicle = vehicleCoverages
                .filter { it !in selectedVehicles }
                .maxByOrNull { coverage -> coverage.coveredZones.count { it in uncoveredZones } }
                ?: return selectedVehicles

            val newlyCoveredZones = bestVehicle.coveredZones.filter { it in uncoveredZones }.toSet()

            if (newlyCoveredZones.isEmpty()) {
                break
            }

            selectedVehicles.add(bestVehicle)
            uncoveredZones.removeAll(newlyCoveredZones)
        }

        return selectedVehicles
    }
}
