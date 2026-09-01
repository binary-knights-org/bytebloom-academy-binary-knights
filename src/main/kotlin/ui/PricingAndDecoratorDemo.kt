package ui

import domain.decorator.ColdChainDecorator
import domain.decorator.ExpressInsuranceDecorator
import domain.decorator.FragileHandlingDecorator
import domain.model.BasePackageComponent
import domain.model.PackageComponent
import domain.model.Route
import domain.model.Warehouse
import domain.pricing.DispatchStrategy
import domain.pricing.EcoStrategy
import domain.pricing.ExpressStrategy
import domain.pricing.FragileStrategy
import domain.usecase.CalculatePricingUseCase

private const val LABEL_PADDING = 12

internal fun runPricingAndDecoratorDemos(
    graph: List<Warehouse>,
    calculatePricingUseCase: CalculatePricingUseCase
) {
    val firstWarehouse = graph.firstOrNull()
    val firstRoute = firstWarehouse?.outgoingRoutes?.firstOrNull()
    val firstPackage = firstWarehouse?.cargoQueue?.firstOrNull()

    if (firstRoute == null || firstPackage == null) return

    val baseComponent = BasePackageComponent(firstPackage)
    println("\n[DYNAMIC PRICING ENGINE]")
    println("------------------------------------------------------------")
    printStrategyResult("Eco", baseComponent, firstRoute, EcoStrategy(), calculatePricingUseCase)
    printStrategyResult("Express", baseComponent, firstRoute, ExpressStrategy(), calculatePricingUseCase)
    printStrategyResult("Fragile", baseComponent, firstRoute, FragileStrategy(), calculatePricingUseCase)

    runDecoratorDemo(baseComponent, firstRoute, calculatePricingUseCase)
}

private fun printStrategyResult(
    label: String,
    pkg: PackageComponent,
    route: Route,
    strategy: DispatchStrategy,
    useCase: CalculatePricingUseCase
) {
    val cost = useCase(pkg, route, strategy)
    val paddedLabel = label.padEnd(LABEL_PADDING)
    println(" $paddedLabel | Final Cost: \$${cost}")
}

private fun runDecoratorDemo(
    pkg: PackageComponent,
    route: Route,
    calculatePricingUseCase: CalculatePricingUseCase
) {
    val baseCost = calculatePricingUseCase(pkg, route, ExpressStrategy())

    val insured = ExpressInsuranceDecorator(pkg)
    val coldChain = ColdChainDecorator(insured)
    val fragile = FragileHandlingDecorator(coldChain)

    println("\n[DECORATOR PATTERN: VALUE-ADDED SERVICES]")
    println("------------------------------------------------------------")
    println(" 1. Base Express Cost      : \$${baseCost}")
    println(" 2. + Insurance            : \$${insured.calculateTransitRate(baseCost)}")
    println(" 3. + Cold Chain           : \$${coldChain.calculateTransitRate(baseCost)}")
    println(" 4. + Fragile Handling     : \$${fragile.calculateTransitRate(baseCost)}")
    println("------------------------------------------------------------")
}
