package domain.state

import domain.model.exception.IllegalStateTransitionException

class DeliveryFailedState : ShipmentState {

    override fun assignToVehicle(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot assign a failed package to a vehicle"
        )
    }

    override fun startTransit(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot start transit for a failed package"
        )
    }

    override fun markDelivered(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot deliver a failed package"
        )
    }

    override fun markFailed(): ShipmentState {
        throw IllegalStateTransitionException(
            "Package has already failed"
        )
    }
}
