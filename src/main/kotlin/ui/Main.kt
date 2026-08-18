package ui
import java.util.Locale


import data.dataholder.PackageRaw
import data.repository.CsvPackageRepository
import data.repository.CsvRouteRepository
import data.repository.CsvVehicleRepository
import data.repository.CsvWarehouseRepository
import domain.algorithm.pathfinding.BidirectionalBfsRouter
import domain.algorithm.pathfinding.LeastHopRouter
import domain.algorithm.pathfinding.OptimalTransitRouter
import domain.algorithm.pathfinding.ShortestPathRouter
import domain.algorithm.sorting.sortPackagesByImportance
import domain.builder.DomainGraphBuilder
import domain.builder.RepositoryProvider
import domain.decorator.ColdChainDecorator
import domain.decorator.ExpressInsuranceDecorator
import domain.decorator.FragileHandlingDecorator
import domain.model.Package
import domain.model.PackageComponent
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
import domain.ring.DeterministicHashingEngine
import domain.ring.breakdown.BreakdownSimulationLogic
import domain.ring.breakdown.VerificationReport

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

private fun printRouteDemo(graph: List<Warehouse>, router: ShortestPathRouter, label: String) {
    println("\n--- $label ---")

    val origin = graph.firstOrNull() ?: return
    val destination = graph.lastOrNull() ?: return
    println("Finding path from ${origin.id} to ${destination.id}...")

    val path = router.findShortestPath(origin, destination)

    if (path == null) {
        println("No path found: ${destination.id} is not reachable from ${origin.id}.")
    } else {
        println("Path (${path.size - 1} hop(s)): ${path.joinToString(" -> ") { it.id }}")
    }
}

private fun printComparisonReport(
    origin: Warehouse,
    destination: Warehouse,
    bfsPath: List<Warehouse>?,
    bfsExecutionTime: Double,
    bfsEvaluatedWarehouses: Int,
    bidirectionalPath: List<Warehouse>?,
    bidirectionalExecutionTime: Double,
    bidirectionalEvaluatedWarehouses: Int
) {
    println()
    println("============================================================")
    println("              ROUTING ALGORITHM COMPARISON")
    println("============================================================")
    println("Origin      : ${origin.id}")
    println("Destination : ${destination.id}")

    printRouterReport(
        name = "Standard BFS (Least-Hop Router)",
        path = bfsPath,
        executionTime = bfsExecutionTime,
        evaluatedWarehouses = bfsEvaluatedWarehouses
    )

    printRouterReport(
        name = "Bidirectional BFS",
        path = bidirectionalPath,
        executionTime = bidirectionalExecutionTime,
        evaluatedWarehouses = bidirectionalEvaluatedWarehouses
    )

    printPathVerification(
        bfsPath = bfsPath,
        bidirectionalPath = bidirectionalPath
    )

    printEfficiencyComparison(
        bfsEvaluatedWarehouses = bfsEvaluatedWarehouses,
        bidirectionalEvaluatedWarehouses =
            bidirectionalEvaluatedWarehouses
    )
}

private fun printRouterReport(
    name: String,
    path: List<Warehouse>?,
    executionTime: Double,
    evaluatedWarehouses: Int
) {
    println()
    println("------------------------------------------------------------")
    println(name)
    println("------------------------------------------------------------")

    if (path == null) {
        println("No path found.")
        return
    }

    println("Path       : ${path.joinToString(" -> ") { it.id }}")
    println("Hops       : ${path.size - 1}")
    println("Evaluated  : $evaluatedWarehouses")
    println("Time       : %.4f ms".format(Locale.US, executionTime))
}

private fun printPathVerification(
    bfsPath: List<Warehouse>?,
    bidirectionalPath: List<Warehouse>?
) {
    println()
    println("------------------------------------------------------------")
    println("Verification")
    println("------------------------------------------------------------")

    if (bfsPath == null || bidirectionalPath == null) {
        println("Cannot compare paths.")
        return
    }

    val bfsHops = bfsPath.size - 1
    val bidirectionalHops = bidirectionalPath.size - 1

    println("BFS Hops                : $bfsHops")
    println("Bidirectional BFS Hops  : $bidirectionalHops")
    println("Same path length        : ${bfsHops == bidirectionalHops}")
}

private fun printEfficiencyComparison(
    bfsEvaluatedWarehouses: Int,
    bidirectionalEvaluatedWarehouses: Int
) {
    if (bfsEvaluatedWarehouses == 0) return

    val improvement =
        (bfsEvaluatedWarehouses - bidirectionalEvaluatedWarehouses) *
                100.0 / bfsEvaluatedWarehouses

    println()
    println("------------------------------------------------------------")
    println("Efficiency")
    println("------------------------------------------------------------")

    println(
        "Bidirectional BFS evaluated " +
                "%.2f%% fewer warehouses."
                    .format(Locale.US, improvement)
    )
}

fun main() {
    val repositories = assembleRepositories(
        CsvVehicleRepository(VEHICLES_FILE_PATH),
        CsvPackageRepository(PACKAGE_FILE_PATH),
        CsvRouteRepository(ROUTES_FILE_PATH),
        CsvWarehouseRepository(WAREHOUSES_FILE_PATH)
    )
    val sortedPackages = sortPackagesByImportance(repositories.packageRepository.getAllPackages())
    printTopShipments(sortedPackages, TOP_SHIPMENTS_LIMIT)

    val graph = buildDomainGraph(repositories)
    printSortedCargoQueueForFirstWarehouse(graph)

    val pricingEngine = RoutePricingEngine(EcoStrategy())
    val expressStrategy = ExpressStrategy()
    val fragileStrategy = FragileStrategy()
    printDispatchStrategyDemo(pricingEngine, expressStrategy, fragileStrategy)
    printBreakdownSimulationDemo(BreakdownSimulationLogic())

    runDecoratorDemo(graph, pricingEngine, expressStrategy)
    printRouteDemo(graph, LeastHopRouter(), "Least-Hop Router Demo (BFS)")
    printRouteDemo(graph, OptimalTransitRouter(), "Optimal Transit Router Demo (Dijkstra)")
    printRouteDemo(graph, BidirectionalBfsRouter(), "Bidirectional Transit Router Demo (Dijkstra)")

    // Composition Root
    val leastHopRouter = LeastHopRouter()
    val bidirectionalRouter = BidirectionalBfsRouter()

    val origin = graph.firstOrNull() ?: return
    val destination = graph.lastOrNull() ?: return

    val bfsStartTime = System.nanoTime()

    val bfsPath = leastHopRouter.findShortestPath(
        origin,
        destination
    )

    val bfsExecutionTime =
        (System.nanoTime() - bfsStartTime) / 1_000_000.0

    val bidirectionalStartTime = System.nanoTime()

    val bidirectionalPath = bidirectionalRouter.findShortestPath(
        origin,
        destination
    )

    val bidirectionalExecutionTime =
        (System.nanoTime() - bidirectionalStartTime) / 1_000_000.0

    printComparisonReport(
        origin = origin,
        destination = destination,
        bfsPath = bfsPath,
        bfsExecutionTime = bfsExecutionTime,
        bfsEvaluatedWarehouses = leastHopRouter.visitedWarehouseCount,
        bidirectionalPath = bidirectionalPath,
        bidirectionalExecutionTime = bidirectionalExecutionTime,
        bidirectionalEvaluatedWarehouses =
            bidirectionalRouter.visitedWarehouseCount
    )
}
