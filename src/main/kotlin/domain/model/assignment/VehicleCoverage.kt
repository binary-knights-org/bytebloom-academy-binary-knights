package domain.model.assignment

import domain.model.RegionalZone
import domain.model.Vehicle

data class VehicleCoverage(
    val vehicle: Vehicle,
    val coveredZones: Set<RegionalZone>
)
