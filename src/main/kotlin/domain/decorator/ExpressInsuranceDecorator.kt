package domain.decorator

import domain.model.PackageComponent

class ExpressInsuranceDecorator(
    component: PackageComponent,
    private val riskPremium: Double = DEFAULT_RISK_PREMIUM
) : PackageDecorator(component) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return super.calculateTransitRate(baseTransitRate) + riskPremium
    }

    private companion object {
        const val DEFAULT_RISK_PREMIUM = 25.0
    }
}
