package domain.algorithm.ring.breakdown

import domain.algorithm.ring.PackageAssignmentRing
import domain.model.BreakdownEvent
import domain.model.Package
import domain.model.Priority
import domain.model.RegionalZone
import domain.model.SimulationResult
import domain.model.Vehicle
import domain.model.VerificationReport
import domain.model.Warehouse
import domain.model.exception.EmptyRingException
import java.util.NavigableMap
import kotlin.math.abs

class BreakdownSimulationLogic {

    fun runSimulation(): SimulationResult {
        val originHub = Warehouse("WH-HUB-A", "Central Hub", RegionalZone.CENTRAL, HUB_LAT, HUB_LNG)
        val destinationHub = Warehouse("WH-HUB-B", "Destination Hub", RegionalZone.NORTH, HUB_LAT + 1, HUB_LNG + 1)
        val ring = setupRing(originHub)
        val packages = createPackages(originHub, destinationHub, PACKAGE_COUNT)

        val context = buildBreakdownContext(ring, TARGET_BROKEN_SLOT)
        val assignmentsBefore = performAssignment(ring, packages)

        ring.removeVehicle(context.slot)

        val assignmentsAfter = performAssignment(ring, packages)

        return SimulationResult(packages, assignmentsBefore, assignmentsAfter, context)
    }

    fun createReport(result: SimulationResult): VerificationReport =
        VerificationReport(
            migratedPackageIds = extractMigratedPackageIds(result),
            brokenVehicleId = result.breakdownEvent.brokenVehicle.id,
            fallbackVehicleId = result.breakdownEvent.fallbackVehicle.id,
            validationError = validateResult(result)
        )

    private fun setupRing(hub: Warehouse): PackageAssignmentRing =
        PackageAssignmentRing().apply {
            addVehicle(SLOT_A, Vehicle("TRK-A", DEFAULT_CAPACITY, DEFAULT_SPEED, hub))
            addVehicle(SLOT_B, Vehicle("TRK-B", DEFAULT_CAPACITY, DEFAULT_SPEED, hub))
            addVehicle(SLOT_C, Vehicle("TRK-C", DEFAULT_CAPACITY, DEFAULT_SPEED, hub))
            addVehicle(SLOT_D, Vehicle("TRK-D", DEFAULT_CAPACITY, DEFAULT_SPEED, hub))
        }

    private fun createPackages(originHub: Warehouse, destinationHub: Warehouse, count: Int): List<Package> =
        List(count) { index ->
            Package(
                id = "PKG-%03d".format(index + 1),
                weight = DEFAULT_WEIGHT,
                priority = DEFAULT_PRIORITY,
                originHub = originHub,
                destinationHub = destinationHub
            )
        }

    private fun buildBreakdownContext(ring: PackageAssignmentRing, brokenSlot: Int): BreakdownEvent {
        val ringMap = ring.getRingMap()
        val brokenVehicle = ringMap[brokenSlot] ?: error("No vehicle at slot $brokenSlot")

        val nextSlot = ringMap.higherKey(brokenSlot) ?: ringMap.firstKey()
        val fallbackVehicle = ringMap[nextSlot] ?: error("No fallback vehicle available")

        return BreakdownEvent(brokenSlot, brokenVehicle, fallbackVehicle)
    }

    private fun performAssignment(ring: PackageAssignmentRing, packages: List<Package>): Map<Package, Vehicle> {
        val ringMap = ring.getRingMap()
        return packages.associateWith { pkg ->
            val slot = calculateSlot(pkg)
            findResponsibleVehicle(ringMap, slot)
                ?: error("No active vehicle available for slot $slot")
        }
    }

    private fun calculateSlot(pkg: Package): Int = abs(pkg.id.hashCode()) % RING_SIZE

    private fun findResponsibleVehicle(ringMap: NavigableMap<Int, Vehicle>, packageSlot: Int): Vehicle? {
        if (ringMap.isEmpty()) {
            throw EmptyRingException()
        }
        val assignedSlot = ringMap.ceilingKey(packageSlot) ?: ringMap.firstKey()
        return ringMap[assignedSlot]
    }

    private fun extractMigratedPackageIds(result: SimulationResult): List<String> =
        result.packages
            .filter { isPackageMigrated(it, result) }
            .map { it.id }

    private fun validateResult(result: SimulationResult): List<String> =
        result.packages.mapNotNull { pkg ->
            if (isPackageMigrated(pkg, result)) {
                validateRerouting(pkg, result)
            } else {
                validateStability(pkg, result)
            }
        }

    private fun isPackageMigrated(pkg: Package, result: SimulationResult): Boolean =
        result.before[pkg] === result.breakdownEvent.brokenVehicle

    private fun validateRerouting(pkg: Package, result: SimulationResult): String? =
        if (result.after[pkg] !== result.breakdownEvent.fallbackVehicle) {
            "Package ${pkg.id} did not land on the correct fallback vehicle."
        } else null

    private fun validateStability(pkg: Package, result: SimulationResult): String? =
        if (result.before[pkg] !== result.after[pkg]) {
            "Package ${pkg.id} on a healthy vehicle was incorrectly moved."
        } else null

    private companion object {
        const val PACKAGE_COUNT = 30
        const val TARGET_BROKEN_SLOT = 40

        const val SLOT_A = 15
        const val SLOT_B = 40
        const val SLOT_C = 65
        const val SLOT_D = 90

        const val RING_SIZE = 100

        const val DEFAULT_WEIGHT = 10.0
        val DEFAULT_PRIORITY = Priority.STANDARD
        const val DEFAULT_CAPACITY = 1000.0
        const val DEFAULT_SPEED = 2.0

        const val HUB_LAT = 32.07
        const val HUB_LNG = -116.21
    }
}
