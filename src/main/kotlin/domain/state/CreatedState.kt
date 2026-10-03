package domain.state

import domain.model.exception.IllegalStateTransitionException

class CreatedState : ShipmentState {

    override fun assignToVehicle(): ShipmentState {
        return AssignedToVehicleState()
    }

    override fun startTransit(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot start transit while package is created"
        )
    }

    override fun markDelivered(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot mark package as delivered while it is created"
        )
    }

    override fun markFailed(): ShipmentState {
        throw IllegalStateTransitionException(
            "Cannot mark package as failed while it is created"
        )
    }
}
