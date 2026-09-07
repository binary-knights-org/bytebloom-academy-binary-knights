package domain.model

class BasePackageComponent: PackageComponent {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return baseTransitRate
    }
}
