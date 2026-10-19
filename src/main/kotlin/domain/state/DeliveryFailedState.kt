package domain.state

class DeliveryFailedState : ShipmentState {
    override fun equals(other: Any?): Boolean = other is DeliveryFailedState
    override fun hashCode(): Int = javaClass.hashCode()
}
