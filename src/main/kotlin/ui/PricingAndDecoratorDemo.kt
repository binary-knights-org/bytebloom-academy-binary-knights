package ui

import domain.decorator.ColdChainDecorator
import domain.decorator.ExpressInsuranceDecorator
import domain.decorator.FragileHandlingDecorator
import domain.model.BasePackageComponent
import domain.model.Package
import domain.model.Route
import domain.model.Warehouse
import domain.pricing.DispatchStrategy
import domain.pricing.EcoStrategy
import domain.pricing.ExpressStrategy
import domain.pricing.FragileStrategy
import domain.usecase.CalculatePricingUseCase
import domain.usecase.PricingRequest

private const val LABEL_PADDING = 12

internal fun runPricingAndDecoratorDemos(
    graph: List<Warehouse>, calculatePricingUseCase: CalculatePricingUseCase
) {
    val firstWarehouse = graph.firstOrNull()
    val firstRoute = firstWarehouse?.outgoingRoutes?.firstOrNull()
    val firstPackage = firstWarehouse?.cargoQueue?.firstOrNull()

    if (firstRoute == null || firstPackage == null) return

    println("\n[DYNAMIC PRICING ENGINE]")
    println("------------------------------------------------------------")

    printStrategyResult(
        "Eco", firstPackage, firstRoute, EcoStrategy(), calculatePricingUseCase
    )

    printStrategyResult(
        "Express", firstPackage, firstRoute, ExpressStrategy(), calculatePricingUseCase
    )

    printStrategyResult(
        "Fragile", firstPackage, firstRoute, FragileStrategy(), calculatePricingUseCase
    )

    runDecoratorDemo(
        firstPackage, firstRoute, calculatePricingUseCase
    )
}

private fun printStrategyResult(
    label: String, pkg: Package, route: Route, strategy: DispatchStrategy, useCase: CalculatePricingUseCase
) {
    val component = BasePackageComponent()

    val request = PricingRequest(
        pkg = pkg, component = component, route = route, strategy = strategy
    )

    val cost = useCase(request)

    println(
        " ${label.padEnd(LABEL_PADDING)} | Final Cost: $$cost"
    )
}

private fun runDecoratorDemo(
    pkg: Package, route: Route, calculatePricingUseCase: CalculatePricingUseCase
) {
    val strategy = ExpressStrategy()
    val baseComponent = BasePackageComponent()
    val baseRequest = PricingRequest(
        pkg = pkg, component = baseComponent, route = route, strategy = strategy
    )

    val baseCost = calculatePricingUseCase(baseRequest)
    val insured = ExpressInsuranceDecorator(baseComponent)
    val coldChain = ColdChainDecorator(insured)
    val fragile = FragileHandlingDecorator(coldChain)

    println("\n[DECORATOR PATTERN: VALUE-ADDED SERVICES]")
    println("------------------------------------------------------------")
    println(" 1. Base Express Cost      : $$baseCost")
    println(" 2. + Insurance            : $${insured.calculateTransitRate(baseCost)}")
    println(" 3. + Cold Chain           : $${coldChain.calculateTransitRate(baseCost)}")
    println(" 4. + Fragile Handling     : $${fragile.calculateTransitRate(baseCost)}")
    println("------------------------------------------------------------")
}
