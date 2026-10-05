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
        currentHub = originWarehouse
    )

    println("\n--- KNAPSACK CARGO DISPATCH DEMO ---")
    printPackagePool(packagePool)
    println("\nVehicle: ${vehicle.id} | Max Capacity: ${vehicle.maxCapacityKg} kg")

    val optimizer = KnapsackCargoOptimizer()
    val selectedPackages = optimizer(
        packages = packagePool,
        maxCapacityKg = vehicle.maxCapacityKg
    )

    printSelectedCargo(selectedPackages, vehicle.maxCapacityKg)
    dispatchSelectedPackages(selectedPackages, vehicle)
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
            destinationHub = destination
        ),
        Package(
            id = "PKG-KNAP-02",
            weight = 15.0,
            priority = Priority.STANDARD,
            originHub = origin,
            destinationHub = destination
        ),
        Package(
            id = "PKG-KNAP-03",
            weight = 20.0,
            priority = Priority.STANDARD,
            originHub = origin,
            destinationHub = destination
        ),
        Package(
            id = "PKG-KNAP-04",
            weight = 30.0,
            priority = Priority.STANDARD,
            originHub = origin,
            destinationHub = destination
        )
    )
}

private fun printPackagePool(packages: List<Package>) {
    println("Package Pool:")
    packages.forEach { pkg ->
        val stateName = pkg.getState()::class.simpleName
        println("  ${pkg.id} | ${pkg.weight} kg | Priority: ${pkg.priority} | State: $stateName")
    }
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
