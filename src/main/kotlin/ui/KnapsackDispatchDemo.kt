package ui

import domain.algorithm.dp.KnapsackCargoOptimizer
import domain.dispatch.StandardDispatchProcessor
import domain.model.Package
import domain.model.Priority
import domain.model.Vehicle
import domain.model.Warehouse

fun runKnapsackDispatchDemo(warehouses: List<Warehouse>) {
    val originWarehouse = warehouses.first()
    val destinationWarehouse = warehouses.last()

    val packagePool = createSamplePackagePool(originWarehouse, destinationWarehouse)
    val vehicle = Vehicle(
        id = "TRK-KNAP-01",
        maxCapacityKg = 40.0,
        costPerKm = 2.5,
        currentHub = originWarehouse,
        maxVolumeM3 = 7.0
    )

    println("\n--- KNAPSACK CARGO DISPATCH DEMO ---")
    printPackagePool(packagePool)
    println(
        "\nVehicle: ${vehicle.id} | " +
                "Max Weight: ${vehicle.maxCapacityKg} kg | " +
                "Max Volume: ${vehicle.maxVolumeM3} m³"
    )

    val optimizer = KnapsackCargoOptimizer()
    val weightOnlyPackages = optimizer(
        packages = packagePool,
        maxCapacityKg = vehicle.maxCapacityKg
    )

    val volumeAwarePackages = vehicle.maxVolumeM3?.let { maxVolume ->
        optimizer.optimize2D(
            packages = packagePool,
            maxCapacityKg = vehicle.maxCapacityKg,
            maxVolumeM3 = maxVolume
        )
    } ?: emptyList()

    printOptimizationComparison(
        weightOnlyPackages = weightOnlyPackages,
        volumeAwarePackages = volumeAwarePackages,
        vehicle = vehicle
    )

    dispatchSelectedPackages(volumeAwarePackages, vehicle)
    printVehicleState(vehicle)
}

private fun createSamplePackagePool(
    origin: Warehouse,
    destination: Warehouse
): List<Package> {
    return listOf(
        Package(
            id = "PKG-KNAP-01",
            weight = 10.0,
            priority = Priority.STANDARD,
            originHub = origin,
            destinationHub = destination,
            volumeM3 = 4.0
        ),
        Package(
            id = "PKG-KNAP-02",
            weight = 15.0,
            priority = Priority.STANDARD,
            originHub = origin,
            destinationHub = destination,
            volumeM3 = 2.0
        ),
        Package(
            id = "PKG-KNAP-03",
            weight = 20.0,
            priority = Priority.STANDARD,
            originHub = origin,
            destinationHub = destination,
            volumeM3 = 3.0
        ),
        Package(
            id = "PKG-KNAP-04",
            weight = 30.0,
            priority = Priority.STANDARD,
            originHub = origin,
            destinationHub = destination,
            volumeM3 = 5.0
        )
    )
}

private fun printPackagePool(packages: List<Package>) {
    println("Package Pool:")
    packages.forEach { pkg ->
        val stateName = pkg.getState()::class.simpleName

        println("  ${pkg.id} | " + "${pkg.weight} kg | " +
                    "${pkg.volumeM3} m³ | " + "Priority: ${pkg.priority} | " + "State: $stateName")
    }
}

private fun printOptimizationComparison(
    weightOnlyPackages: List<Package>,
    volumeAwarePackages: List<Package>,
    vehicle: Vehicle
) {
    println("\n--- OPTIMIZATION COMPARISON ---")
    println("\nWeight Only (1D Knapsack):")
    weightOnlyPackages.forEach { pkg -> println("  ${pkg.id} | ${pkg.weight} kg | ${pkg.volumeM3} m³") }

    val weightOnlyTotalWeight = weightOnlyPackages.sumOf { it.weight }
    val weightOnlyTotalVolume = weightOnlyPackages.sumOf { it.volumeM3 ?: 0.0 }

    println("  Total: $weightOnlyTotalWeight kg | " + "$weightOnlyTotalVolume m³")

    println("\nWeight + Volume (2D Knapsack):")
    volumeAwarePackages.forEach { pkg ->
        println("  ${pkg.id} | ${pkg.weight} kg | ${pkg.volumeM3} m³")
    }

    val volumeAwareTotalWeight = volumeAwarePackages.sumOf { it.weight }
    val volumeAwareTotalVolume = volumeAwarePackages.sumOf { it.volumeM3 ?: 0.0 }

    println("  Total: $volumeAwareTotalWeight kg / ${vehicle.maxCapacityKg} kg | " +
                "$volumeAwareTotalVolume m³ / ${vehicle.maxVolumeM3} m³")
}

private fun printSelectedCargo(selectedPackages: List<Package>, maxCapacityKg: Double) {
    println("\nSelected Cargo by Knapsack:")
    selectedPackages.forEach { pkg ->
        println("  ${pkg.id} | ${pkg.weight} kg | Priority: ${pkg.priority}")
    }

    val totalWeight = selectedPackages.sumOf { it.weight }
    println("\nTotal Selected Weight: $totalWeight kg / $maxCapacityKg kg")
}

private fun dispatchSelectedPackages(
    selectedPackages: List<Package>,
    vehicle: Vehicle
) {
    val dispatchProcessor = StandardDispatchProcessor()
    println("\nDispatching Selected Cargo...")

    selectedPackages.forEach { pkg ->
        val previousState = pkg.getState()::class.simpleName
        val notification = dispatchProcessor.dispatch(pkg = pkg, vehicle = vehicle)
        val newState = pkg.getState()::class.simpleName

        println("  ${pkg.id}: $previousState -> $newState")
        notification?.let {
            println("    Notification: ${it.message}")
        }
    }
}

private fun printVehicleState(vehicle: Vehicle) {
    println("\nVehicle State:")
    println("  ${vehicle.id}: ${vehicle.currentLoadKg} kg / ${vehicle.maxCapacityKg} kg")
    println("  Loaded packages: ${vehicle.loadedCargo.size}")
    println("------------------------------------------------------------")
}
