package domain.model

import domain.algorithm.sortPackagesByWeightDescending

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

    fun addPackage(pkg: Package) {
        _cargoQueue.add(pkg)
    }

    fun addRoute(route: Route) {
        _outgoingRoutes.add(route)
    }

    fun addVehicle(vehicle: Vehicle) {
        _stationedVehicles.add(vehicle)
    }

    fun sortCargoQueueByWeightDescending() {
        sortPackagesByWeightDescending(_cargoQueue)
    }
}
