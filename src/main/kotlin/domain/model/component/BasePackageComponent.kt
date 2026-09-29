package domain.model.component

class BasePackageComponent: PackageComponent {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return baseTransitRate
    }
}
