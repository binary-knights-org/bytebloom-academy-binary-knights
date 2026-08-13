package domain.decorator

import domain.model.PackageComponent

private const val DEFAULT_RISK_PREMIUM = 25.0
class ExpressInsuranceDecorator(
    wrappedPackage: PackageComponent,
    private val riskPremium: Double = DEFAULT_RISK_PREMIUM
) : PackageDecorator(wrappedPackage) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return decoratedBaseRate(baseTransitRate) + riskPremium
    }
}
