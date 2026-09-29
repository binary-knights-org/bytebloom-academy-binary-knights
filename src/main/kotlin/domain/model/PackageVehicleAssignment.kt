package domain.model


data class PackageVehicleAssignment(
    val packages: List<Package>,
    val vehicle: Vehicle
)