package domain.model

class Package(
    override val id: String,
    override val weight: Double,
    override val priority: String,
    override val originHub: Warehouse,
    override val destinationHub: Warehouse
) : PackageComponent {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return baseTransitRate
    }
}
