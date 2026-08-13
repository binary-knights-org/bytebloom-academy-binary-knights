package domain.decorator

import domain.model.PackageComponent


class ColdChainDecorator(
    wrappedPackage: PackageComponent,
    private val refrigerationMultiplier: Double = 1.18
) : PackageDecorator(wrappedPackage) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return decoratedBaseRate(baseTransitRate) * refrigerationMultiplier
    }
}
