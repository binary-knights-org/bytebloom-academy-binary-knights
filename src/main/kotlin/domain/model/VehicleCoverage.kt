package domain.model

data class VehicleCoverage(
    val vehicle: Vehicle,
    val coveredZones: Set<RegionalZone>
)
