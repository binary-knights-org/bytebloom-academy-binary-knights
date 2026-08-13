package domain.model

interface PackageComponent {
    val id: String
    val weight: Double
    val priority: String
    val originHub: Warehouse
    val destinationHub: Warehouse

    fun calculateTransitRate(baseTransitRate: Double): Double
}
