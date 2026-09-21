package domain.model

import domain.model.exception.InvalidVehicleCapacityException
import domain.model.exception.InvalidVehicleCostException
import kotlin.uuid.Uuid

data class Vehicle(
    val id: String = "$VEHICLE_ID_PREFIX${Uuid.random()}",
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
) {

    init {
        validateCapacity()
        validateCost()
    }

    private fun validateCapacity() {
        if (maxCapacityKg <= MIN_CAPACITY_KG) {
            throw InvalidVehicleCapacityException()
        }
    }

    private fun validateCost() {
        if (costPerKm < MIN_COST_PER_KM) {
            throw InvalidVehicleCostException()
        }
    }

    private val mutableLoadedCargo = mutableListOf<Package>()
    val loadedCargo: List<Package> = mutableLoadedCargo

    val currentLoadKg: Double
        get() = mutableLoadedCargo.sumOf { it.weight }

    fun loadPackage(pkg: Package): Boolean {
        if (currentLoadKg + pkg.weight <= maxCapacityKg) {
            mutableLoadedCargo.add(pkg)
            return true
        }
        return false
    }

    fun restoreCargo(packages: List<Package>) {
        mutableLoadedCargo.clear()
        mutableLoadedCargo.addAll(packages)
    }

    companion object{
        const val VEHICLE_ID_PREFIX = "TRK-"
        const val MIN_CAPACITY_KG = 0.0
        const val MIN_COST_PER_KM = 0.0
    }
}
