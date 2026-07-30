package domain.model

data class Route(
    val id: String,
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val origin: Warehouse,
    val destination: Warehouse
   ){

    override fun toString(): String {
        return "Route(id=$id, distanceKm=$distanceKm, typicalDelayMin=$typicalDelayMin)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Route) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
