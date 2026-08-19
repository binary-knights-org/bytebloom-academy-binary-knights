package ui

fun main() {
    val repositories = initializeRepositories()
    val graph = buildDomainGraph(repositories)
    runCargoDemos(repositories, graph)

    runPricingAndDecoratorDemos(graph)

    runBreakdownSimulationDemo()

    runRoutingAndComparisonDemos(graph)
}
