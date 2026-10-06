package domain.model

import domain.model.exception.InvalidPackageVolumeException
import domain.model.exception.InvalidPackageWeightException
import domain.model.exception.SameOriginAndDestinationException
import domain.state.CreatedState
import domain.state.ShipmentState
import kotlin.uuid.Uuid

data class Package(
    val id: String = "$PACKAGE_ID_PREFIX${Uuid.random()}",
    val weight: Double,
    val priority: Priority,
    val originHub: Warehouse,
    val destinationHub: Warehouse,
    val volumeM3: Double? = null
) {
    private var currentState: ShipmentState = CreatedState()

    init {
        validateWeight()
        validateVolume()
        validateHubs()
    }

    fun setState(state: ShipmentState) {
        currentState = state
    }

    fun getState(): ShipmentState = currentState

    fun assignToVehicle() {
        currentState = currentState.assignToVehicle()
    }

    fun startTransit() {
        currentState = currentState.startTransit()
    }

    fun markDelivered() {
        currentState = currentState.markDelivered()
    }

    fun markFailed() {
        currentState = currentState.markFailed()
    }

    private fun validateWeight() {
        if (weight <= MIN_WEIGHT) {
            throw InvalidPackageWeightException()
        }
    }

    private fun validateVolume() {
        if (volumeM3 != null && volumeM3 <= MIN_VOLUME_M3) {
            throw InvalidPackageVolumeException()
        }
    }

    private fun validateHubs() {
        if (originHub.id == destinationHub.id) {
            throw SameOriginAndDestinationException(
                "Origin and destination hubs cannot be the same"
            )
        }
    }

    companion object {
        const val PACKAGE_ID_PREFIX = "PKG-"
        const val MIN_WEIGHT = 0.0
        const val MIN_VOLUME_M3 = 0.0
    }
}