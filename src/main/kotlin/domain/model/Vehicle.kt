package domain.model

data class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse
)
{


    override fun toString(): String {
        return "Vehicle(id=$id, maxCapacityKg=$maxCapacityKg, costPerKm=$costPerKm)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vehicle) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
