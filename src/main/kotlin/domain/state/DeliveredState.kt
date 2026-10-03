package domain.state

import domain.model.exception.IllegalStateTransitionException

class DeliveredState : ShipmentState {

    override fun assignToVehicle(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot assign a delivered package to a vehicle"
        )
    }

    override fun startTransit(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot start transit for a delivered package"
        )
    }

    override fun markDelivered(): ShipmentState {
        throw IllegalStateTransitionException(
            "Package is already delivered"
        )
    }

    override fun markFailed(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot mark a delivered package as failed"
        )
    }
}
