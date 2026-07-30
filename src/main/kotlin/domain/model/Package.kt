package domain.model

data class Package (
    val id : String,
    val weight: Double,
    val priority: String,
    val origin: Warehouse,
    val destination: Warehouse
)
{

    override fun toString(): String {
        return "Package(id=$id, weight=$weight, priority=$priority)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Package) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
