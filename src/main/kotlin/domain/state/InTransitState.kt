package domain.state

import domain.model.exception.IllegalStateTransitionException

class InTransitState : ShipmentState {

    override fun assignToVehicle(): ShipmentState {
        throw IllegalStateTransitionException(
            "Package is already in transit"
        )
    }

    override fun startTransit(): ShipmentState {
        throw IllegalStateTransitionException(
            "Package is already in transit"
        )
    }

    override fun markDelivered(): ShipmentState {
        return DeliveredState()
    }

    override fun markFailed(): ShipmentState {
        return DeliveryFailedState()
    }
}
