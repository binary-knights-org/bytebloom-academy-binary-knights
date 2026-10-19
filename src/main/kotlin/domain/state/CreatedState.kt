package domain.state

class CreatedState : ShipmentState {
    override fun assignToVehicle(): ShipmentState = AssignedToVehicleState()

    override fun equals(other: Any?): Boolean = other is CreatedState
    override fun hashCode(): Int = javaClass.hashCode()
}
