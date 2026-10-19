package domain.state

import domain.model.exception.IllegalStateTransitionException

sealed interface ShipmentState {

    fun assignToVehicle(): ShipmentState =
        throw IllegalStateTransitionException("Cannot assign to vehicle from state: ${this::class.simpleName}")

    fun startTransit(): ShipmentState =
        throw IllegalStateTransitionException("Cannot start transit from state: ${this::class.simpleName}")

    fun markDelivered(): ShipmentState =
        throw IllegalStateTransitionException("Cannot mark as delivered from state: ${this::class.simpleName}")

    fun markFailed(): ShipmentState =
        throw IllegalStateTransitionException("Cannot mark as failed from state: ${this::class.simpleName}")
}
