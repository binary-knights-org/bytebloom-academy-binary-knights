package domain.model

import domain.algorithm.sorting.sortPackagesDescendingByWeight
import domain.exception.EntityValidationException
import java.util.UUID

data class Warehouse private constructor(
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

    fun removePackage(pkg: Package): Boolean {
        return _cargoQueue.remove(pkg)
    }

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

        const val ID_PREFIX = "WH-"

        private const val MIN_LATITUDE = -90.0
        private const val MAX_LATITUDE = 90.0
        private const val MIN_LONGITUDE = -180.0
        private const val MAX_LONGITUDE = 180.0



        fun isValidId(id: String) : Boolean {
            if (id.isBlank()) return false

            val hasValidPrefix = id.startsWith(ID_PREFIX)

            val isUUID = runCatching {
                UUID.fromString(id)
            }.isSuccess

            return hasValidPrefix || isUUID
        }

        fun isValidName(name: String): Boolean {
            if (name.isBlank())
                return false
            return true
        }

        fun isValidRegionalZone( regionalZone: String): Boolean {
            if (regionalZone.isBlank())
                return false
            return true
        }


        fun isValidLatitude(latitude: Double): Boolean {
            if (latitude in MIN_LATITUDE..MAX_LATITUDE)
                return true
            return false
        }

        fun isValidlongitude(longitude: Double): Boolean {
            if (longitude in MIN_LONGITUDE..MAX_LONGITUDE)
                return true
            return false
        }


        fun create(
            id: String,
            name: String,
            regionalZone: String,
            latitude: Double,
            longitude: Double
        ) : Warehouse {

            val validationError = when {
                !isValidId(id) ->
                    "Invalid ID format ,,, Must start with $ID_PREFIX or be a valid UUID."

                !isValidName(name) ->
                    "Invalid name format ."

                !isValidRegionalZone(regionalZone) ->
                    "Invalid regionalZone format  "

                !isValidLatitude(latitude) ->
                    "Invalid latitude format "

                !isValidlongitude(longitude) ->
                    "Invalid longitude format  "


                else -> null
            }

            if(validationError != null){
                throw EntityValidationException(validationError)
            }

            return Warehouse(
                id = id,
                name = name,
                regionalZone = regionalZone,
                latitude = latitude,
                longitude = longitude
            )
        }
    }
}
