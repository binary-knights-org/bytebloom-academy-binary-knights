package domain.model.assignment

import domain.model.Package
import domain.model.Route
import domain.model.component.PackageComponent
import domain.pricing.DispatchStrategy

data class PricingRequest(
    val pkg: Package,
    val component: PackageComponent,
    val route: Route,
    val strategy: DispatchStrategy
)
