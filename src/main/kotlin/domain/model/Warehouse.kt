package domain.model

class Warehouse(
    val id: String,
    val name: String,
    val regionalZone: String
) {

    private val _cargoQueue = mutableListOf<Package>()
    private val _outgoingRoutes = mutableListOf<Route>()
    private val _stationedVehicles = mutableListOf<Vehicle>()

    val cargoQueue: List<Package> get() = _cargoQueue
    val outgoingRoutes: List<Route> get() = _outgoingRoutes
    val stationedVehicles: List<Vehicle> get() = _stationedVehicles

    fun addPackage(pkg: Package) { _cargoQueue.add(pkg) }
    fun addRoute(route: Route) { _outgoingRoutes.add(route) }
    fun addVehicle(vehicle: Vehicle) { _stationedVehicles.add(vehicle) }
}
