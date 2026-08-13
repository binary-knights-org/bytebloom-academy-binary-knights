package ui

import data.dataholder.PackageRaw
import data.repository.CsvFleetRepository
import data.repository.CsvPackageRepository
import data.repository.CsvRouteRepository
import data.repository.CsvWarehouseRepository
import domain.algorithm.sortPackagesByImportance
import domain.builder.DomainGraphBuilder
import domain.builder.RepositoryProvider
import domain.decorator.ColdChainDecorator
import domain.decorator.ExpressInsuranceDecorator
import domain.decorator.FragileHandlingDecorator
import domain.model.Package
import domain.model.Vehicle
import domain.model.Warehouse
import domain.pricing.EcoStrategy
import domain.pricing.ExpressStrategy
import domain.pricing.FragileStrategy
import domain.pricing.RoutePricingEngine
import domain.repository.FleetRepository
import domain.repository.PackageRepository
import domain.repository.RouteRepository
import domain.repository.WarehouseRepository
import domain.ring.DeterministicHashingEngine
import domain.ring.VerificationReport

private const val PACKAGE_FILE_PATH = "src/main/resources/packages.csv"
private const val WAREHOUSES_FILE_PATH = "src/main/resources/warehouses.csv"
private const val ROUTES_FILE_PATH = "src/main/resources/routes.csv"
private const val FLEET_FILE_PATH = "src/main/resources/fleet.csv"

private const val TOP_SHIPMENTS_LIMIT = 3
private const val DEMO_WEIGHT_KG = 10.0
private const val DEMO_DISTANCE_KM = 50.0


private fun loadRawData(
    fleetRepository: FleetRepository,
    packageRepository: PackageRepository,
    routeRepository: RouteRepository,
    warehouseRepository: WarehouseRepository
): RepositoryProvider {
    val rawData = RepositoryProvider(
        fleetRepository = fleetRepository,
        packageRepository = packageRepository,
        routeRepository = routeRepository,
        warehouseRepository = warehouseRepository
    )
    printParsingReport(rawData)
    return rawData
}

private fun printParsingReport(rawData: RepositoryProvider) {
    println("\n--- Data Parsing Report ---")
    println(" Successfully parsed Fleet: ${rawData.fleetRepository.getAllFleets().size} vehicle records.")
    println(" Successfully parsed Packages: ${rawData.packageRepository.getAllPackages().size} records.")
    println(" Successfully parsed Routes: ${rawData.routeRepository.getAllRoutes().size} records.")
    println(" Successfully parsed Warehouses: ${rawData.warehouseRepository.getAllWarehouses().size} records.")
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


private fun buildDomainGraph(rawData: RepositoryProvider): List<Warehouse> {
    val graph = DomainGraphBuilder(rawData).buildGraph()
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


private fun printDispatchStrategyDemo() {
    println("\n--- Dispatch Strategy Demo ---")

    val pricingEngine = RoutePricingEngine(EcoStrategy())
    printStrategyResult("Eco", pricingEngine)

    pricingEngine.setStrategy(ExpressStrategy())
    printStrategyResult("Express", pricingEngine)

    pricingEngine.setStrategy(FragileStrategy())
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
    println("\n=== VERIFICATION REPORT ===")
    println("Packages migrated from broken vehicle: ${report.migratedPackageIds.size}")

    if (report.migratedPackageIds.isNotEmpty()) {
        println(
            "SUCCESS: The following packages successfully moved from " +
                    "${report.brokenVehicleId} to ${report.fallbackVehicleId}:"
        )
        println(" -> ${report.migratedPackageIds.joinToString(", ")}")
    }
}

private fun printDecoratorCostDemo(graph: List<Warehouse>) {
    println("\n--- Decorator Pattern Cost Demo ---")

    val firstWarehouse = graph.firstOrNull() ?: return
    val firstRoute = firstWarehouse.outgoingRoutes.first()
    val firstPackage = firstWarehouse.cargoQueue.first()

    val pricingEngine = RoutePricingEngine(ExpressStrategy())
    val baseCost = pricingEngine.calculateCost(firstPackage.weight, firstRoute.distanceKm)
    println("Base Express Cost > $baseCost $")

    val insuredPackage = ExpressInsuranceDecorator(firstPackage)
    val insuredCost = insuredPackage.calculateTransitRate(baseCost)
    println("With Express Insurance > $insuredCost $")

    val coldChainPackage = ColdChainDecorator(insuredPackage)
    val coldChainCost = coldChainPackage.calculateTransitRate(insuredCost)
    println("With Insurance & Cold Chain > $coldChainCost $")

    val fragilePackage = FragileHandlingDecorator(coldChainPackage)
    val finalCost = fragilePackage.calculateTransitRate(coldChainCost)
    println("With Insurance, Cold Chain & Fragile > $finalCost $")
}

fun main() {


    val fleetRepository: FleetRepository = CsvFleetRepository(FLEET_FILE_PATH)
    val packageRepository: PackageRepository = CsvPackageRepository(PACKAGE_FILE_PATH)
    val routeRepository: RouteRepository = CsvRouteRepository(ROUTES_FILE_PATH)
    val warehouseRepository: WarehouseRepository = CsvWarehouseRepository(WAREHOUSES_FILE_PATH)

    val rawData =
        loadRawData(fleetRepository, packageRepository, routeRepository, warehouseRepository)

    val sortedPackages = sortPackagesByImportance(rawData.packageRepository.getAllPackages())
    printTopShipments(sortedPackages, TOP_SHIPMENTS_LIMIT)

    val graph = buildDomainGraph(rawData)
    printSortedCargoQueueForFirstWarehouse(graph)

    printDispatchStrategyDemo()

    val simulationLogic = domain.ring.BreakdownSimulationLogic()
    val result = simulationLogic.runSimulation()

    printAssignments(result.before, "--- Initial Assignment BEFORE Breakdown ---")
    println(
        "\nRemoving broken vehicle at slot ${result.breakdownEvent.slot} " +
                "(${result.breakdownEvent.brokenVehicle.id})..."
    )
    printAssignments(result.after, "--- Re-routing Assignment AFTER Breakdown ---")

    val report = simulationLogic.createReport(result)
    printVerificationReport(report)

    printDecoratorCostDemo(graph)
}
