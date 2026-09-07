package domain.decorator

import domain.model.PackageComponent

abstract class PackageDecorator(
    val component: PackageComponent
) : PackageComponent {
    override fun calculateTransitRate(baseTransitRate: Double): Double {
        return component.calculateTransitRate(baseTransitRate)
    }
}
