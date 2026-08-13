package domain.decorator

import domain.model.PackageComponent

class ExpressInsuranceDecorator(
    wrappedPackage: PackageComponent,
    private val riskPremium: Double = 25.0
) : PackageDecorator(wrappedPackage) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return decoratedBaseRate(baseTransitRate) + riskPremium
    }
}
