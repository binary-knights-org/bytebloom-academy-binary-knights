package domain.decorator

import domain.model.PackageComponent
import domain.model.Warehouse

abstract class PackageDecorator(
    val wrappedPackage: PackageComponent
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
