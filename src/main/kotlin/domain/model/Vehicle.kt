package domain.model

import domain.exception.EntityValidationException
import java.util.UUID

data class Vehicle private constructor(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
) {

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

    companion object {

        const val ID_PREFIX = "TRK-"
        const val MIN_CAPACITY_KG = 0.0
        const val MIN_COST_PER_KM = 0.0


        fun isValidId(id: String) : Boolean {
            if (id.isBlank()) return false

            val hasValidPrefix = id.startsWith(ID_PREFIX)

            val isUUID = runCatching {
                UUID.fromString(id)
            }.isSuccess

             return hasValidPrefix || isUUID
        }

        fun isValidCapacity(maxCapacityKg: Double): Boolean {
            if (maxCapacityKg > MIN_CAPACITY_KG )
                return true
            return false
        }

        fun isValidCostPerKm( costPerKm: Double): Boolean {
            if ( costPerKm > MIN_COST_PER_KM )
                return true
            return false
        }

        fun create(
            id: String,
            maxCapacityKg: Double,
            costPerKm: Double,
            currentHub: Warehouse
        ) : Vehicle {

            val validationError = when {
                !isValidId(id) ->
                    "Invalid ID format ,,, Must start with $ID_PREFIX or be a valid UUID."
                !isValidCapacity(maxCapacityKg) ->
                    "Max capacity must be greater than $MIN_CAPACITY_KG."
                !isValidCostPerKm(costPerKm) ->
                    "Cost per km must be greater than $MIN_COST_PER_KM "

                else -> null
            }

            if(validationError != null){
                throw EntityValidationException(validationError)
            }

            return Vehicle(
                id = id,
                maxCapacityKg = maxCapacityKg,
                costPerKm = costPerKm,
                currentHub = currentHub
            )

        }
    }
}

