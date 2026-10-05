package ui

import domain.algorithm.dp.KnapsackCargoOptimizer
import domain.dispatch.StandardDispatchProcessor
import domain.model.Package
import domain.model.Priority
import domain.model.Vehicle

fun runKnapsackDispatchDemo(warehouses: List<domain.model.Warehouse>) {
    val originWarehouse = warehouses.first()
    val destinationWarehouse = warehouses.last()

    val packagePool = listOf(
        Package(
            id = "PKG-KNAP-01",
            weight = 10.0,
            priority = Priority.STANDARD,
            originHub = originWarehouse,
            destinationHub = destinationWarehouse
        ),
        Package(
            id = "PKG-KNAP-02",
            weight = 15.0,
            priority = Priority.STANDARD,
            originHub = originWarehouse,
            destinationHub = destinationWarehouse
        ),
        Package(
            id = "PKG-KNAP-03",
            weight = 20.0,
            priority = Priority.STANDARD,
            originHub = originWarehouse,
            destinationHub = destinationWarehouse
        ),
        Package(
            id = "PKG-KNAP-04",
            weight = 30.0,
            priority = Priority.STANDARD,
            originHub = originWarehouse,
            destinationHub = destinationWarehouse
        )
    )

    val vehicle = Vehicle(
        id = "TRK-KNAP-01",
        maxCapacityKg = 40.0,
        costPerKm = 2.5,
        currentHub = originWarehouse
    )

    println("\n--- KNAPSACK CARGO DISPATCH DEMO ---")
    println("Package Pool:")
    packagePool.forEach { pkg ->
        val stateName = pkg.getState()::class.simpleName
        println("  ${pkg.id} | ${pkg.weight} kg | Priority: ${pkg.priority} | State: $stateName")
    }

    println("\nVehicle: ${vehicle.id} | Max Capacity: ${vehicle.maxCapacityKg} kg")

    val optimizer = KnapsackCargoOptimizer()
    val selectedPackages = optimizer(
        packages = packagePool,
        maxCapacityKg = vehicle.maxCapacityKg
    )

    println("\nSelected Cargo by Knapsack:")
    selectedPackages.forEach { pkg ->
        println("  ${pkg.id} | ${pkg.weight} kg | Priority: ${pkg.priority}")
    }

    val totalWeight = selectedPackages.sumOf { it.weight }
    println("\nTotal Selected Weight: $totalWeight kg / ${vehicle.maxCapacityKg} kg")

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

    println("\nVehicle State:")
    println("  ${vehicle.id}: ${vehicle.currentLoadKg} kg / ${vehicle.maxCapacityKg} kg")
    println("  Loaded packages: ${vehicle.loadedCargo.size}")
    println("------------------------------------------------------------")
}
