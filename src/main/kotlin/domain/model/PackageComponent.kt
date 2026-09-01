package domain.model

interface PackageComponent {
    fun calculateTransitRate(baseTransitRate: Double): Double
}
