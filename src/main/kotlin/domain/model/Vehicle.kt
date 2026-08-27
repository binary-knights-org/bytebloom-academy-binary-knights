package domain.model

data class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
) {

    private val mutableLoadedCargo = mutableListOf<Package>()

    val loadedCargo: List<Package>
        get() = mutableLoadedCargo

    val currentLoadKg: Double
        get() = mutableLoadedCargo.sumOf { it.weight }

    val remainingCapacityKg: Double
        get() = maxCapacityKg - currentLoadKg

    fun loadPackage(pkg: Package): Boolean {
        if (currentLoadKg + pkg.weight <= maxCapacityKg) {
            mutableLoadedCargo.add(pkg)
            return true
        }
        return false
    }

    fun clearCargo() {
        mutableLoadedCargo.clear()
    }

}
