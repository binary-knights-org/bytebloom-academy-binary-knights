package domain.decorator

import domain.model.PackageComponent


class FragileHandlingDecorator(
    wrappedPackage: PackageComponent,
    private val protectiveFee: Double = 35.0
) : PackageDecorator(wrappedPackage) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return decoratedBaseRate(baseTransitRate) + protectiveFee
    }
}