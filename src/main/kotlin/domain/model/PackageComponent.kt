package domain.model

interface PackageComponent {
    val packages: Package
    fun calculateTransitRate(baseTransitRate: Double): Double
}
