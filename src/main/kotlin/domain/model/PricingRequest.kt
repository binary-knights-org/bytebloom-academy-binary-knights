package domain.model

import domain.pricing.DispatchStrategy

data class PricingRequest(
    val pkg: Package,
    val component: PackageComponent,
    val route: Route,
    val strategy: DispatchStrategy
)
