package domain.model

data class Warehouse(
    val id: String,
    val name: String,
    val regionalZone: String,
    val cargoQueue: List<Package> = emptyList(),
    val outgoingRoutes: List<Route> = emptyList(),
    val stationedVehicles: List<Vehicle> = emptyList()
){

    override fun toString(): String {
        return "Warehouse(id=$id, name=$name, regionalZone=$regionalZone)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Warehouse) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}


