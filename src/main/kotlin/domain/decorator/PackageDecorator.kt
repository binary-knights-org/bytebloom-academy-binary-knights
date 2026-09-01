package domain.decorator

import domain.model.PackageComponent

abstract class PackageDecorator(
    val component: PackageComponent
) : PackageComponent by component {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return component.calculateTransitRate(baseTransitRate)
    }
}
