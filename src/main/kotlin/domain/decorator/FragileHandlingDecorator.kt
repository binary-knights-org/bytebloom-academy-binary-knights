package domain.decorator

import domain.model.PackageComponent

private const val DEFAULT_PROTECTIVE_FEE = 35.0

class FragileHandlingDecorator(
    component: PackageComponent,
    private val protectiveFee: Double = DEFAULT_PROTECTIVE_FEE
) : PackageDecorator(component) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return super.calculateTransitRate(baseTransitRate) + protectiveFee
    }
}
