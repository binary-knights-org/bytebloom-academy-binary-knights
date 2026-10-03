package domain.state

interface ShipmentState {

    fun assignToVehicle(): ShipmentState

    fun startTransit(): ShipmentState

    fun markDelivered(): ShipmentState

    fun markFailed(): ShipmentState
}
