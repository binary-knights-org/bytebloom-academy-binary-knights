package domain.model

import domain.algorithm.sorting.sortPackagesDescendingByWeight
import domain.exception.BlankFieldException
import domain.validator.IdValidator
import domain.exception.InvalidCoordinateException

private const val WAREHOUSE_ID_PREFIX = "WH-"
private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0

data class Warehouse(
    val id: String,
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
        validateId()
        validateStrings()
        validateCoordinates()
    }

    private fun validateId() {
        val errors = IdValidator.validate(id, WAREHOUSE_ID_PREFIX, "Warehouse")
        if (errors.isNotEmpty()) throw errors.first()
    }

    private fun validateStrings() {
        if (name.isBlank()) throw BlankFieldException("Warehouse name")
        if (regionalZone.isBlank()) throw BlankFieldException("Regional zone")
    }

    private fun validateCoordinates() {
        if (latitude !in MIN_LATITUDE..MAX_LATITUDE) {
            throw InvalidCoordinateException("Latitude", MIN_LATITUDE, MAX_LATITUDE)
        }
        if (longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
            throw InvalidCoordinateException("Longitude", MIN_LONGITUDE, MAX_LONGITUDE)
        }
    }

    fun addPackage(pkg: Package) { _cargoQueue.add(pkg) }

    fun removePackage(pkg: Package): Boolean = _cargoQueue.remove(pkg)

    fun addRoute(route: Route) { _outgoingRoutes.add(route) }

    fun addVehicle(vehicle: Vehicle) { _stationedVehicles.add(vehicle) }

    fun sortCargoQueueByWeightDescending() { sortPackagesDescendingByWeight(_cargoQueue) }

    fun restoreCargoQueue(packages: List<Package>) {
        _cargoQueue.clear()
        _cargoQueue.addAll(packages)
    }
}
