package domain.model.component

interface PackageComponent {
    fun calculateTransitRate(baseTransitRate: Double): Double
}
