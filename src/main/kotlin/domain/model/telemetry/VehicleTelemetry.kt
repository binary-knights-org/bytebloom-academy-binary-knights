package domain.model.telemetry

import domain.model.exception.InvalidFuelPercentageException
import domain.model.exception.InvalidLatitudeException
import domain.model.exception.InvalidLongitudeException
import domain.model.exception.InvalidSpeedException
import domain.model.exception.InvalidVehicleIdException

data class VehicleTelemetry(
    val vehicleId: String,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Double,
    val fuelPercentage: Double,
    val timestamp: Long = System.currentTimeMillis()
) {
    init {
        validateVehicleId()
        validateCoordinates()
        validateSpeed()
        validateFuel()
    }

    private fun validateVehicleId() {
        if (vehicleId.isBlank()) {
            throw InvalidVehicleIdException()
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

    private fun validateSpeed() {
        if (speedKmh < MIN_SPEED) {
            throw InvalidSpeedException()
        }
    }

    private fun validateFuel() {
        if (fuelPercentage !in MIN_FUEL..MAX_FUEL) {
            throw InvalidFuelPercentageException()
        }
    }

    val isStationary: Boolean
        get() = speedKmh == 0.0

    val isLowFuel: Boolean
        get() = fuelPercentage < LOW_FUEL_THRESHOLD

    companion object {
        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0
        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0
        const val MIN_SPEED = 0.0
        const val MIN_FUEL = 0.0
        const val MAX_FUEL = 100.0
        const val LOW_FUEL_THRESHOLD = 15.0
    }
}
