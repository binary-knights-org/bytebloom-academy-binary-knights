package domain.decorator

import domain.model.PackageComponent
import domain.model.Warehouse

abstract class PackageDecorator(
    private val wrappedPackage: PackageComponent
) : PackageComponent {
    override val id: String
        get() = wrappedPackage.id

    override val weight: Double
        get() = wrappedPackage.weight

    override val priority: String
        get() = wrappedPackage.priority

    override val originHub: Warehouse
        get() = wrappedPackage.originHub

    override val destinationHub: Warehouse
        get() = wrappedPackage.destinationHub

    protected fun decoratedBaseRate(baseTransitRate: Double): Double {
        return wrappedPackage.calculateTransitRate(baseTransitRate)
    }
}

class FragileHandlingDecorator(
    wrappedPackage: PackageComponent,
    private val protectiveFee: Double = 35.0
) : PackageDecorator(wrappedPackage) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return decoratedBaseRate(baseTransitRate) + protectiveFee
    }
}

class ColdChainDecorator(
    wrappedPackage: PackageComponent,
    private val refrigerationMultiplier: Double = 1.18
) : PackageDecorator(wrappedPackage) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return decoratedBaseRate(baseTransitRate) * refrigerationMultiplier
    }
}

class ExpressInsuranceDecorator(
    wrappedPackage: PackageComponent,
    private val riskPremium: Double = 25.0
) : PackageDecorator(wrappedPackage) {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return decoratedBaseRate(baseTransitRate) + riskPremium
    }
}

