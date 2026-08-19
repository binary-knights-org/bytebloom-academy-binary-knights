package ui

import domain.decorator.ColdChainDecorator
import domain.decorator.ExpressInsuranceDecorator
import domain.decorator.FragileHandlingDecorator
import domain.model.PackageComponent
import domain.model.Warehouse
import domain.pricing.EcoStrategy
import domain.pricing.ExpressStrategy
import domain.pricing.FragileStrategy
import domain.pricing.RoutePricingEngine

internal const val DEMO_WEIGHT_KG = 10.0
internal const val DEMO_DISTANCE_KM = 50.0

internal fun runPricingAndDecoratorDemos(graph: List<Warehouse>) {
    val pricingEngine = RoutePricingEngine(EcoStrategy())
    val expressStrategy = ExpressStrategy()
    val fragileStrategy = FragileStrategy()

    printDispatchStrategyDemo(pricingEngine, expressStrategy, fragileStrategy)
    runDecoratorDemo(graph, pricingEngine, expressStrategy)
}

private fun printDispatchStrategyDemo(
    pricingEngine: RoutePricingEngine,
    expressStrategy: ExpressStrategy,
    fragileStrategy: FragileStrategy
) {
    println("\n--- Dispatch Strategy Demo ---")
    printStrategyResult("Eco", pricingEngine)

    pricingEngine.setStrategy(expressStrategy)
    printStrategyResult("Express", pricingEngine)

    pricingEngine.setStrategy(fragileStrategy)
    printStrategyResult("Fragile", pricingEngine)
}

private fun printStrategyResult(label: String, engine: RoutePricingEngine) {
    val cost = engine.calculateCost(weight = DEMO_WEIGHT_KG, distance = DEMO_DISTANCE_KM)
    val priority = engine.getPriority()
    println("$label Strategy -> Cost = $cost, Priority Multiplier = $priority")
}

private fun runDecoratorDemo(
    graph: List<Warehouse>,
    pricingEngine: RoutePricingEngine,
    expressStrategy: ExpressStrategy
) {
    val firstWarehouse = graph.firstOrNull()
    val firstRoute = firstWarehouse?.outgoingRoutes?.firstOrNull()
    val firstPackage = firstWarehouse?.cargoQueue?.firstOrNull()

    if (firstRoute != null && firstPackage != null) {
        pricingEngine.setStrategy(expressStrategy)
        val baseCost = pricingEngine.calculateCost(firstPackage.weight, firstRoute.distanceKm)
        val insuredPackage = ExpressInsuranceDecorator(firstPackage)
        val coldChainPackage = ColdChainDecorator(insuredPackage)
        val fragilePackage = FragileHandlingDecorator(coldChainPackage)

        printDecoratorCostDemo(baseCost, insuredPackage, coldChainPackage, fragilePackage)
    }
}

private fun printDecoratorCostDemo(
    baseCost: Double,
    insuredPackage: PackageComponent,
    coldChainPackage: PackageComponent,
    fragilePackage: PackageComponent
) {
    println("\n--- Decorator Pattern Cost Demo ---")
    println("Base Express Cost = $baseCost $")

    val insuredCost = insuredPackage.calculateTransitRate(baseCost)
    println("With Express Insurance = $insuredCost $")

    val coldChainCost = coldChainPackage.calculateTransitRate(insuredCost)
    println("With Insurance & Cold Chain = $coldChainCost $")

    val finalCost = fragilePackage.calculateTransitRate(coldChainCost)
    println("With Insurance, Cold Chain & Fragile = $finalCost $")
}
