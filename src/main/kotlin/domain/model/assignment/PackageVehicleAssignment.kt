package domain.model.assignment

import domain.model.Package
import domain.model.Vehicle

data class PackageVehicleAssignment(
    val packages: List<Package>,
    val vehicle: Vehicle
)
