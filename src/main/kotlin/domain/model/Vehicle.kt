package domain.model

import domain.validator.IdValidator
import domain.model.exception.InvalidCostPerKmException
import domain.model.exception.InvalidMaxCapacityException
import java.util.UUID

private const val VEHICLE_ID_PREFIX = "TRK-"
private const val MIN_CAPACITY_KG = 0.0
private const val MIN_COST_PER_KM = 0.0

data class Vehicle(
    val id: String = "$VEHICLE_ID_PREFIX${UUID.randomUUID()}",
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
) {

    private val mutableLoadedCargo = mutableListOf<Package>()
    val loadedCargo: List<Package> = mutableLoadedCargo

    val currentLoadKg: Double
        get() = mutableLoadedCargo.sumOf { it.weight }

    init {
        validateId()
        validateCapacity()
        validateCost()
    }

    private fun validateId() {
        val errors = IdValidator.validate(id, VEHICLE_ID_PREFIX, "Vehicle")
        if (errors.isNotEmpty()) throw errors.first()
    }

    private fun validateCapacity() {
        if (maxCapacityKg <= MIN_CAPACITY_KG) throw InvalidMaxCapacityException(MIN_CAPACITY_KG)
    }

    private fun validateCost() {
        if (costPerKm <= MIN_COST_PER_KM) throw InvalidCostPerKmException(MIN_COST_PER_KM)
    }

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
}
