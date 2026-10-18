package domain.state

class DeliveredState : ShipmentState {
    override fun equals(other: Any?): Boolean = other is DeliveredState
    override fun hashCode(): Int = javaClass.hashCode()
}
