package ui

import data.dataholder.PackageRaw
import data.repository.CsvPackageRepository
import data.repository.CsvRouteRepository
import data.repository.CsvVehicleRepository
import data.repository.CsvWarehouseRepository
import domain.algorithm.LeastHopRouter
import domain.algorithm.OptimalTransitRouter
import domain.algorithm.sortPackagesByImportance
import domain.builder.DomainGraphBuilder
import domain.builder.RepositoryProvider
import domain.decorator.ColdChainDecorator
import domain.decorator.ExpressInsuranceDecorator
import domain.decorator.FragileHandlingDecorator
import domain.model.PackageComponent
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle
import domain.model.Warehouse
import domain.pricing.EcoStrategy
import domain.pricing.ExpressStrategy
import domain.pricing.FragileStrategy
import domain.pricing.RoutePricingEngine
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.VehicleRepository
import domain.repository.WarehouseRepository
import domain.ring.BreakdownSimulationLogic
import domain.ring.DeterministicHashingEngine
import domain.ring.VerificationReport

private const val PACKAGE_FILE_PATH = "src/main/resources/packages.csv"
private const val WAREHOUSES_FILE_PATH = "src/main/resources/warehouses.csv"
private const val ROUTES_FILE_PATH = "src/main/resources/routes.csv"
private const val VEHICLES_FILE_PATH = "src/main/resources/fleet.csv"

private const val TOP_SHIPMENTS_LIMIT = 3
private const val DEMO_WEIGHT_KG = 10.0
private const val DEMO_DISTANCE_KM = 50.0


private fun assembleRepositories(
    vehicleRepository: VehicleRepository,
    packageRepository: PackageRepository,
    routeRepository: RouteRepository,
    warehouseRepository: WarehouseRepository
): RepositoryProvider {
    val repositories = RepositoryProvider(
        vehicleRepository = vehicleRepository,
        packageRepository = packageRepository,
        routeRepository = routeRepository,
        warehouseRepository = warehouseRepository
    )
    printParsingReport(repositories)
    return repositories
}

private fun printParsingReport(repositories: RepositoryProvider) {
    println("\n--- Data Parsing Report ---")
    println(" Successfully parsed Fleet: ${repositories.vehicleRepository.getAllVehicles().size} vehicle records.")
    println(" Successfully parsed Packages: ${repositories.packageRepository.getAllPackages().size} records.")
    println(" Successfully parsed Routes: ${repositories.routeRepository.getAllRoutes().size} records.")
    println(" Successfully parsed Warehouses: ${repositories.warehouseRepository.getAllWarehouses().size} records.")
}

private fun printTopShipments(packages: List<PackageRaw>, limit: Int) {
    println("\n--- Executing Manual Package Sorting ---")
    println("\n--- Top $limit Priority Shipments ---")

    packages.take(limit).forEachIndexed { index, pkg ->
        val packageNumber = index + 1
        println(
            "package = $packageNumber" +
                    " , id = ${pkg.packageId}" +
                    " , destinationHub = ${pkg.destinationHubId}" +
                    " , weight = ${pkg.weight}" +
                    " kg , priority = ${pkg.priority}"
        )
    }
}

private fun buildDomainGraph(repositories: RepositoryProvider): List<Warehouse> {
    val graph = DomainGraphBuilder(repositories).buildGraph()
    printGraphSummary(graph)
    return graph
}

private fun printGraphSummary(warehouses: List<Warehouse>) {
    println("\n--- Domain Graph Summary ---")
    println("Total Warehouses Built: ${warehouses.size}")

    for (warehouse in warehouses) {
        println("\nHub: ${warehouse.id} (${warehouse.name}) | Zone: ${warehouse.regionalZone}")
        println("  Stationed Vehicles: ${warehouse.stationedVehicles.size}")
        println("  Cargo Queue: ${warehouse.cargoQueue.size}")
        println("  Outgoing Routes: ${warehouse.outgoingRoutes.size}")
    }
}

private fun printSortedCargoQueueForFirstWarehouse(warehouse: List<Warehouse>) {
    val warehouse = warehouse.firstOrNull()
    if (warehouse == null) {
        println("\nNo warehouse available to sort cargo queue.")
        return
    }

    warehouse.sortCargoQueueByWeightDescending()

    println("\n--- Sorted Cargo Queue (Warehouse: ${warehouse.id}) ---")
    warehouse.cargoQueue.forEach { pkg ->
        println("id = ${pkg.id}, weight = ${pkg.weight} kg")
    }
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

private fun printAssignments(assignments: Map<Package, Vehicle>, title: String) {
    println("\n$title")
    assignments.forEach { (pkg, vehicle) ->
        val slot = DeterministicHashingEngine.calculateSlot(pkg)
        println("  - ${pkg.id} (Slot %02d) -> Assigned to ${vehicle.id}".format(slot))
    }
}

private fun printVerificationReport(report: VerificationReport) {
    println("\n--- Verification Report ---")
    println("Packages migrated from broken vehicle: ${report.migratedPackageIds.size}")

    if (report.migratedPackageIds.isNotEmpty()) {
        println(
            "SUCCESS: The following packages successfully moved from " +
                    "${report.brokenVehicleId} to ${report.fallbackVehicleId}:"
        )
        println(" -> ${report.migratedPackageIds.joinToString(", ")}")
    }
}

private fun printBreakdownSimulationDemo(simulationLogic: BreakdownSimulationLogic) {
    val result = simulationLogic.runSimulation()

    printAssignments(result.before, "--- Initial Assignment BEFORE Breakdown ---")
    println(
        "\nRemoving broken vehicle at slot ${result.breakdownEvent.slot} " +
                "(${result.breakdownEvent.brokenVehicle.id})..."
    )
    printAssignments(result.after, "--- Re-routing Assignment AFTER Breakdown ---")

    val report = simulationLogic.createReport(result)
    printVerificationReport(report)
}
private fun printDecoratorCostDemo(
    pricingEngine:RoutePricingEngine,
    firstRoute:Route ,
    firstPackage:Package,
    insuredPackage: PackageComponent,
    coldChainPackage: PackageComponent,
    fragilePackage: PackageComponent
    ) {
    println("\n--- Decorator Pattern Cost Demo ---")


    val baseCost = pricingEngine.calculateCost(firstPackage.weight, firstRoute.distanceKm)
    println("Base Express Cost > $baseCost $")

    val insuredCost = insuredPackage.calculateTransitRate(baseCost)
    println("With Express Insurance > $insuredCost $")

    val coldChainCost = coldChainPackage.calculateTransitRate(insuredCost)
    println("With Insurance & Cold Chain > $coldChainCost $")

    val finalCost = fragilePackage.calculateTransitRate(coldChainCost)
    println("With Insurance, Cold Chain & Fragile > $finalCost $")
}

private fun printLeastHopRouteDemo(graph: List<Warehouse>, router: LeastHopRouter) {
    println("\n--- Least-Hop Router Demo (BFS) ---")

    val origin = graph.firstOrNull() ?: return
    val destination = graph.lastOrNull() ?: return
    println("Finding shortest path from ${origin.id} to ${destination.id}...")

    val path = router.findShortestPath(origin, destination)

    if (path == null) {
        println("No path found: ${destination.id} is not reachable from ${origin.id}.")
    } else {
        println("Shortest path (${path.size - 1} hop(s)): ${path.joinToString(" -> ") { it.id }}")
    }
}

private fun printOptimalTransitRouteDemo(graph: List<Warehouse>, router: OptimalTransitRouter) {
    println("\n--- Optimal Transit Router Demo (Dijkstra) ---")

    val origin = graph.firstOrNull() ?: return
    val destination = graph.lastOrNull() ?: return
    println("Finding optimal path (shortest distance) from ${origin.id} to ${destination.id}...")

    val path = router.findShortestPath(origin, destination)

    if (path == null) {
        println("No path found: ${destination.id} is not reachable from ${origin.id}.")
    } else {
        println("Optimal path (${path.size - 1} hop(s)): ${path.joinToString(" -> ") { it.id }}")
    }
}

fun main() {

    val vehicleRepository: VehicleRepository = CsvVehicleRepository(VEHICLES_FILE_PATH)
    val packageRepository: PackageRepository = CsvPackageRepository(PACKAGE_FILE_PATH)
    val routeRepository: RouteRepository = CsvRouteRepository(ROUTES_FILE_PATH)
    val warehouseRepository: WarehouseRepository = CsvWarehouseRepository(WAREHOUSES_FILE_PATH)
        val ecoStrategy = EcoStrategy()
        val expressStrategy = ExpressStrategy()
        val fragileStrategy = FragileStrategy()
        val pricingEngine = RoutePricingEngine(ecoStrategy)
        val leastHopRouter = LeastHopRouter()
        val optimalTransitRouter = OptimalTransitRouter()
        val simulationLogic = BreakdownSimulationLogic()
    val repositories = assembleRepositories(vehicleRepository, packageRepository, routeRepository, warehouseRepository)
    val sortedPackages = sortPackagesByImportance(repositories.packageRepository.getAllPackages())
        printTopShipments(sortedPackages, TOP_SHIPMENTS_LIMIT)
    val graph = buildDomainGraph(repositories)
        printSortedCargoQueueForFirstWarehouse(graph)
        printDispatchStrategyDemo(pricingEngine, expressStrategy, fragileStrategy)
        printBreakdownSimulationDemo(simulationLogic)
    val firstWarehouse = graph.firstOrNull()
    val firstRoute = firstWarehouse?.outgoingRoutes?.firstOrNull()
    val firstPackage = firstWarehouse?.cargoQueue?.firstOrNull()
    if (firstRoute != null && firstPackage != null) {
        val expressPricingEngine = RoutePricingEngine(expressStrategy)
        val insuredPackage = ExpressInsuranceDecorator(firstPackage)
        val coldChainPackage = ColdChainDecorator(insuredPackage)
        val fragilePackage = FragileHandlingDecorator(coldChainPackage)
        printDecoratorCostDemo(expressPricingEngine, firstRoute, firstPackage, insuredPackage, coldChainPackage, firstPackage)
        printLeastHopRouteDemo(graph, leastHopRouter)
        printOptimalTransitRouteDemo(graph, optimalTransitRouter)
    }
}
