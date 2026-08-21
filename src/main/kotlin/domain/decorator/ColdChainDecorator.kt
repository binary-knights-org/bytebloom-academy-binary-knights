package domain.decorator

import domain.model.PackageComponent


private const val DEFAULT_REFRIGERATION_MULTIPLIER = 1.18
class ColdChainDecorator(
    wrappedPackage: PackageComponent,
    private val refrigerationMultiplier: Double = DEFAULT_REFRIGERATION_MULTIPLIER
) : PackageDecorator(wrappedPackage) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return decoratedBaseRate(baseTransitRate) * refrigerationMultiplier
    }
}
