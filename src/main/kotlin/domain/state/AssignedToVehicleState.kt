package domain.state

class AssignedToVehicleState : ShipmentState {
    override fun startTransit(): ShipmentState = InTransitState()

    override fun equals(other: Any?): Boolean = other is AssignedToVehicleState
    override fun hashCode(): Int = javaClass.hashCode()
}
