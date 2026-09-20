package domain.algorithm.greedy

import domain.model.RegionalZone
import domain.model.VehicleCoverage

class GreedyFleetDispatcher {

    fun dispatch(
        targetZones: Set<RegionalZone>,
        vehicleCoverages: List<VehicleCoverage>
    ): List<VehicleCoverage> {

        val uncoveredZones = targetZones.toMutableSet()
        val selectedVehicles = mutableListOf<VehicleCoverage>()

        /*
         * Greedy checks the available vehicles at each selection step.
         * In the worst case, up to N vehicles are checked for up to N selections,
         * resulting in O(N²) time complexity.
         *
         * A brute-force set-covering approach would examine vehicle subsets,
         * resulting in O(2^N) time complexity.
        */

        while (uncoveredZones.isNotEmpty()) {

            val bestVehicle = vehicleCoverages
                .filter { it !in selectedVehicles }
                .maxByOrNull { coverage -> coverage.coveredZones.count { it in uncoveredZones } }
                ?: return selectedVehicles

            val newlyCoveredZones = bestVehicle.coveredZones.filter { it in uncoveredZones }.toSet()

            if (newlyCoveredZones.isEmpty()) {
                return selectedVehicles
            }

            selectedVehicles.add(bestVehicle)
            uncoveredZones.removeAll(newlyCoveredZones)
        }

        return selectedVehicles
    }
}