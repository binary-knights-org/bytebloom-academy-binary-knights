package domain.ring

import domain.model.Package
import domain.model.Vehicle
import domain.model.Warehouse

private const val PACKAGE_COUNT = 30
private const val TARGET_BROKEN_SLOT = 40

private const val SLOT_A = 15
private const val SLOT_B = 40
private const val SLOT_C = 65
private const val SLOT_D = 90

private const val DEFAULT_WEIGHT = 10.0
private const val DEFAULT_PRIORITY = "STANDARD"
private const val DEFAULT_CAPACITY = 1000.0
private const val DEFAULT_SPEED = 2.0

private const val HUB_LAT = 32.07
private const val HUB_LNG = -116.21

fun main() {
    val hub = Warehouse("WH-HUB", "Central Hub", "N/A", HUB_LAT, HUB_LNG)
    val ring = setupRing(hub)
    val packages = createPackages(hub, PACKAGE_COUNT)

    runBreakdownSimulation(ring, packages, TARGET_BROKEN_SLOT)
}

private fun setupRing(hub: Warehouse): PackageAssignmentRing {
    val ring = PackageAssignmentRing().apply {
        addVehicle(SLOT_A, Vehicle("TRK-A", DEFAULT_CAPACITY, DEFAULT_SPEED, hub))
        addVehicle(SLOT_B, Vehicle("TRK-B", DEFAULT_CAPACITY, DEFAULT_SPEED, hub))
        addVehicle(SLOT_C, Vehicle("TRK-C", DEFAULT_CAPACITY, DEFAULT_SPEED, hub))
        addVehicle(SLOT_D, Vehicle("TRK-D", DEFAULT_CAPACITY, DEFAULT_SPEED, hub))
    }

    println("Ring Initialized with 4 Vehicles (Slots: $SLOT_A, $SLOT_B, $SLOT_C, $SLOT_D)")
    return ring
}

private fun createPackages(hub: Warehouse, count: Int): List<Package> {
    return List(count) { index ->
        Package(
            id = "PKG-%03d".format(index + 1),
            weight = DEFAULT_WEIGHT,
            priority = DEFAULT_PRIORITY,
            originHub = hub,
            destinationHub = hub
        )
    }
}

private fun runBreakdownSimulation(ring: PackageAssignmentRing, packages: List<Package>, targetSlot: Int) {
    val context = buildBreakdownContext(ring, targetSlot)
    val assignmentsBefore = assignAndPrint(ring, packages, "--- Initial Assignment BEFORE Breakdown ---")
    executeBreakdown(ring, context)

    val assignmentsAfter = assignAndPrint(ring, packages, "--- Re-routing Assignment AFTER Breakdown ---")
    val simulationResult = SimulationResult(packages, assignmentsBefore, assignmentsAfter, context)
    verifyResultsInSinglePass(simulationResult)
}

private fun buildBreakdownContext(ring: PackageAssignmentRing, brokenSlot: Int): BreakdownEvent {
    val ringMap = ring.getRingMap()
    val brokenVehicle = ringMap[brokenSlot] ?: error("No vehicle at slot $brokenSlot")

    val nextSlot = ringMap.higherKey(brokenSlot) ?: ringMap.firstKey()
    val fallbackVehicle = ringMap[nextSlot]!!

    return BreakdownEvent(brokenSlot, brokenVehicle, fallbackVehicle)
}

private fun executeBreakdown(ring: PackageAssignmentRing, context: BreakdownEvent) {
    println("\nRemoving broken vehicle at slot ${context.slot} (${context.brokenVehicle.id})...")
    ring.removeVehicle(context.slot)
}

private fun assignAndPrint(
    ring: PackageAssignmentRing,
    packages: List<Package>,
    title: String
): Map<Package, Vehicle> {
    println("\n$title")
    val assignments = performAssignment(ring, packages)
    printAssignments(assignments)
    return assignments
}

private fun performAssignment(ring: PackageAssignmentRing, packages: List<Package>): Map<Package, Vehicle> {
    val ringMap = ring.getRingMap()
    return packages.associateWith { pkg ->
        val slot = DeterministicHashingEngine.calculateSlot(pkg)
        ClockwiseRouter.findResponsibleVehicle(ringMap, slot)
            ?: error("No active vehicle available for slot $slot")
    }
}

private fun printAssignments(assignments: Map<Package, Vehicle>) {
    assignments.forEach { (pkg, vehicle) ->
        val slot = DeterministicHashingEngine.calculateSlot(pkg)
        println("  - ${pkg.id} (Slot %02d) -> Assigned to ${vehicle.id}".format(slot))
    }
}

private fun verifyResultsInSinglePass(result: SimulationResult) {
    val migratedPackageIds = mutableListOf<String>()

    for (pkg in result.packages) {
        when {
            isPackageMigrated(pkg, result) -> migratedPackageIds.add(pkg.id)
        }

        validateRerouting(pkg, result)
        validateStability(pkg, result)
    }

    printVerificationReport(migratedPackageIds, result.breakdownEvent)
}

private fun isPackageMigrated(pkg: Package, result: SimulationResult): Boolean {
    return result.before[pkg] === result.breakdownEvent.brokenVehicle
}

private fun validateRerouting(pkg: Package, result: SimulationResult) {
    val wasOnBrokenVehicle = result.before[pkg] === result.breakdownEvent.brokenVehicle
    val isNowOnFallback = result.after[pkg] === result.breakdownEvent.fallbackVehicle

    if (!wasOnBrokenVehicle) return

    check(isNowOnFallback) { "FAIL: Package ${pkg.id} did not land on the correct fallback vehicle!" }
}

private fun validateStability(pkg: Package, result: SimulationResult) {
    val wasOnBrokenVehicle = result.before[pkg] === result.breakdownEvent.brokenVehicle
    val truckBefore = result.before[pkg]
    val truckAfter = result.after[pkg]

    if (wasOnBrokenVehicle) return

    check(truckBefore === truckAfter) { "FAIL: Package ${pkg.id} on a healthy vehicle was incorrectly moved!" }
}

private fun printVerificationReport(migratedPackageIds: List<String>, event: BreakdownEvent) {
    println("\n=== VERIFICATION REPORT ===")
    println("Packages migrated from broken vehicle: ${migratedPackageIds.size}")

    if (migratedPackageIds.isNotEmpty()) {
        println("SUCCESS: The following packages successfully moved from ${event.brokenVehicle.id} to ${event.fallbackVehicle.id}:")
        println(" -> ${migratedPackageIds.joinToString(", ")}")
    }
}
