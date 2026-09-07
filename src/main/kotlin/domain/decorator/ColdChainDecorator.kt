package domain.decorator

import domain.model.PackageComponent

class ColdChainDecorator(
    component: PackageComponent,
    private val refrigerationMultiplier: Double = DEFAULT_REFRIGERATION_MULTIPLIER
) : PackageDecorator(component) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return super.calculateTransitRate(baseTransitRate) * refrigerationMultiplier
    }

    private companion object {
        const val DEFAULT_REFRIGERATION_MULTIPLIER = 1.18
    }
}
