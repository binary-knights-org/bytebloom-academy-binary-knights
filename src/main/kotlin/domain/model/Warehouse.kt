package domain.model

import domain.algorithm.sorting.sortPackagesDescendingByWeight
import domain.exception.EntityValidationException
import domain.validator.FieldViolation
import domain.validator.IdValidator
import java.util.UUID

private const val WAREHOUSE_ID_PREFIX = "WH-"
private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0

data class Warehouse(
    val id: String = "$WAREHOUSE_ID_PREFIX${UUID.randomUUID()}",
    val name: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
) {

    private val _cargoQueue = mutableListOf<Package>()
    private val _outgoingRoutes = mutableListOf<Route>()
    private val _stationedVehicles = mutableListOf<Vehicle>()

    val cargoQueue: List<Package> = _cargoQueue
    val outgoingRoutes: List<Route> = _outgoingRoutes
    val stationedVehicles: List<Vehicle> = _stationedVehicles

    init {
        val violations = validateWarehouse(id, name, regionalZone, latitude, longitude)
        if (violations.isNotEmpty()) {
            throw EntityValidationException(violations)
        }
    }

    fun validateWarehouse(
        id: String, name: String, regionalZone: String, latitude: Double, longitude: Double
    ): List<FieldViolation> {
        val violations = mutableListOf<FieldViolation>()
        violations.addAll(IdValidator.validate(id, WAREHOUSE_ID_PREFIX, "Warehouse"))

        if (name.isBlank()) {
            violations.add(FieldViolation("name", "Warehouse name cannot be blank."))
        }
        if (regionalZone.isBlank()) {
            violations.add(FieldViolation("regionalZone", "Regional zone cannot be blank."))
        }
        if (latitude !in MIN_LATITUDE..MAX_LATITUDE) {
            violations.add(FieldViolation("latitude", "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE."))
        }
        if (longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
            violations.add(
                FieldViolation(
                    "longitude", "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE."
                )
            )
        }
        return violations
    }

    fun addPackage(pkg: Package) {
        _cargoQueue.add(pkg)
    }

    fun removePackage(pkg: Package): Boolean = _cargoQueue.remove(pkg)

    fun addRoute(route: Route) {
        _outgoingRoutes.add(route)
    }

    fun addVehicle(vehicle: Vehicle) {
        _stationedVehicles.add(vehicle)
    }

    fun sortCargoQueueByWeightDescending() {
        sortPackagesDescendingByWeight(_cargoQueue)
    }

    fun restoreCargoQueue(packages: List<Package>) {
        _cargoQueue.clear()
        _cargoQueue.addAll(packages)
    }
}
