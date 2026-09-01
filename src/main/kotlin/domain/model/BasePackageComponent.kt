package domain.model

class BasePackageComponent(
    override val packages: Package
): PackageComponent {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return baseTransitRate
    }
}
