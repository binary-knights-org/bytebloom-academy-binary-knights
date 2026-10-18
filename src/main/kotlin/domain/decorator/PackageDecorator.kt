package domain.decorator

import domain.model.component.PackageComponent

abstract class PackageDecorator(
    val component: PackageComponent
) : PackageComponent by component
