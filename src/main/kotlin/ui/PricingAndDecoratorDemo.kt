package ui

import domain.decorator.ColdChainDecorator
import domain.decorator.ExpressInsuranceDecorator
import domain.decorator.FragileHandlingDecorator
import domain.model.Warehouse
import domain.pricing.EcoStrategy
import domain.pricing.ExpressStrategy
import domain.pricing.FragileStrategy
import domain.pricing.RoutePricingEngine

internal const val DEMO_WEIGHT_KG = 10.0
internal const val DEMO_DISTANCE_KM = 50.0
private const val LABEL_PADDING = 12
private const val LABEL_PADDING_HALF = 6

internal fun runPricingAndDecoratorDemos(graph: List<Warehouse>) {
    val pricingEngine = RoutePricingEngine(EcoStrategy())

    println("\n[DYNAMIC PRICING ENGINE]")
    println("------------------------------------------------------------")
    printStrategyResult("Eco", pricingEngine)
    pricingEngine.setStrategy(ExpressStrategy())
    printStrategyResult("Express", pricingEngine)
    pricingEngine.setStrategy(FragileStrategy())
    printStrategyResult("Fragile", pricingEngine)

    runDecoratorDemo(graph, pricingEngine)
}

private fun printStrategyResult(label: String, engine: RoutePricingEngine) {
    val cost = engine.calculateCost(weight = DEMO_WEIGHT_KG, distance = DEMO_DISTANCE_KM)
    val priority = engine.getPriority()
    val paddedLabel = label.padEnd(LABEL_PADDING)
    println(
        " $paddedLabel | Cost: \$${cost.toString().padEnd(LABEL_PADDING_HALF)}" +
                " | Priority Multiplier: ${priority}x"
    )
}

private fun runDecoratorDemo(graph: List<Warehouse>, pricingEngine: RoutePricingEngine) {
    val firstWarehouse = graph.firstOrNull()
    val firstRoute = firstWarehouse?.outgoingRoutes?.firstOrNull()
    val firstPackage = firstWarehouse?.cargoQueue?.firstOrNull()

    if (firstRoute != null && firstPackage != null) {
        pricingEngine.setStrategy(ExpressStrategy())
        val baseCost = pricingEngine.calculateCost(firstPackage.weight, firstRoute.distanceKm)

        val insuredPackage = ExpressInsuranceDecorator(firstPackage)
        val coldChainPackage = ColdChainDecorator(insuredPackage)
        val fragilePackage = FragileHandlingDecorator(coldChainPackage)

        val baseTransitRate = insuredPackage.calculateTransitRate(baseCost)

        println("\n[DECORATOR PATTERN: VALUE-ADDED SERVICES]")
        println("------------------------------------------------------------")
        println(" 1. Base Express Cost            : \$${baseCost}")
        println(" 2. + Express Insurance          : \$${insuredPackage.calculateTransitRate(baseCost)}")
        println(
            " 3. + Cold Chain (Refrigeration) : \$${
                coldChainPackage.calculateTransitRate(
                    baseTransitRate
                )
            }"
        )
        println(
            " 4. + Fragile Handling           : \$${
                fragilePackage.calculateTransitRate(
                    coldChainPackage.calculateTransitRate(baseTransitRate)
                )
            }"
        )
        println("------------------------------------------------------------")
    }
}
