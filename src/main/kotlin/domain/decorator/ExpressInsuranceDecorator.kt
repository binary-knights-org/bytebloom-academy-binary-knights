package domain.decorator

import domain.model.PackageComponent

private const val DEFAULT_RISK_PREMIUM = 25.0
class ExpressInsuranceDecorator(
    component: PackageComponent,
    private val riskPremium: Double = DEFAULT_RISK_PREMIUM
) : PackageDecorator(component) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return super.calculateTransitRate(baseTransitRate) + riskPremium
    }
}
