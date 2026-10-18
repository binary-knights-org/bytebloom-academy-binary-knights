package domain.state

class InTransitState : ShipmentState {
    override fun markDelivered(): ShipmentState = DeliveredState()
    override fun markFailed(): ShipmentState = DeliveryFailedState()

    override fun equals(other: Any?): Boolean = other is InTransitState
    override fun hashCode(): Int = javaClass.hashCode()
}
