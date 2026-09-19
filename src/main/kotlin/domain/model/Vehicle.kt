package domain.model

import domain.exception.EntityValidationException
import domain.validator.FieldViolation
import domain.validator.IdValidator
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
        val violations = validateVehicle(id, maxCapacityKg, costPerKm)
        if (violations.isNotEmpty()) {
            throw EntityValidationException(violations)
        }
    }

    companion object {
        fun validateVehicle(
            id: String,
            maxCapacityKg: Double,
            costPerKm: Double
        ): List<FieldViolation> {
            val violations = mutableListOf<FieldViolation>()
            violations.addAll(IdValidator.validate(id, VEHICLE_ID_PREFIX, "Vehicle"))
            if (maxCapacityKg <= MIN_CAPACITY_KG) {
                violations.add(FieldViolation("maxCapacityKg", "Max capacity must be greater than $MIN_CAPACITY_KG."))
            }
            if (costPerKm <= MIN_COST_PER_KM) {
                violations.add(FieldViolation("costPerKm", "Cost per km must be greater than $MIN_COST_PER_KM."))
            }
            return violations
        }

        fun create(
            id: String = "$VEHICLE_ID_PREFIX${UUID.randomUUID()}",
            maxCapacityKg: Double,
            costPerKm: Double,
            currentHub: Warehouse
        ): Result<Vehicle> = runCatching {
            Vehicle(id, maxCapacityKg, costPerKm, currentHub)
        }
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
