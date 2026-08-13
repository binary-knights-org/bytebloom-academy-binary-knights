package domain.decorator

import domain.model.PackageComponent

private const val DEFAULT_PROTECTIVE_FEE = 35.0

class FragileHandlingDecorator(
    wrappedPackage: PackageComponent,
    private val protectiveFee: Double = DEFAULT_PROTECTIVE_FEE
) : PackageDecorator(wrappedPackage) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return decoratedBaseRate(baseTransitRate) + protectiveFee
    }
}
