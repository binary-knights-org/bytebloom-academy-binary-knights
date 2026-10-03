package domain.state

import domain.model.exception.IllegalStateTransitionException

class AssignedToVehicleState : ShipmentState {

    override fun assignToVehicle(): ShipmentState {
        throw IllegalStateTransitionException(
            "Package is already assigned to a vehicle"
        )
    }

    override fun startTransit(): ShipmentState {
        return InTransitState()
    }

    override fun markDelivered(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot deliver package before starting transit"
        )
    }

    override fun markFailed(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot fail package before starting transit"
        )
    }
}
