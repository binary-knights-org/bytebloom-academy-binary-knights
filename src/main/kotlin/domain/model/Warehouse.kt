package domain.model

import domain.algorithm.sorting.sortPackagesDescendingByWeight
import domain.model.exception.InvalidLatitudeException
import domain.model.exception.InvalidLongitudeException
import domain.model.exception.InvalidWarehouseTextException
import kotlin.uuid.Uuid

data class Warehouse(
    val id: String = "$WAREHOUSE_ID_PREFIX${Uuid.random()}",
    val name: String,
    val regionalZone: RegionalZone,
    val latitude: Double,
    val longitude: Double
) {

    init {
        validateTextInputs()
        validateCoordinates()
    }

    private fun validateTextInputs() {
        if (name.isBlank()) {
            throw InvalidWarehouseTextException()
        }
        if (regionalZone.isBlank()) {
            throw InvalidWarehouseTextException()
        }
    }

    private fun validateCoordinates() {
        if (latitude !in MIN_LATITUDE..MAX_LATITUDE) {
            throw InvalidLatitudeException()
        }
        if (longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
            throw InvalidLongitudeException()
        }
    }

    private val _cargoQueue = mutableListOf<Package>()
    private val _outgoingRoutes = mutableListOf<Route>()
    private val _stationedVehicles = mutableListOf<Vehicle>()

    val cargoQueue: List<Package> = _cargoQueue
    val outgoingRoutes: List<Route> = _outgoingRoutes
    val stationedVehicles: List<Vehicle> = _stationedVehicles

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

    companion object {
        const val WAREHOUSE_ID_PREFIX = "WH-"
        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0
        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0
    }
}
