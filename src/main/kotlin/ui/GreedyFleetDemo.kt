package ui

import domain.algorithm.greedy.GreedyFleetDispatcher
import domain.model.RegionalZone
import domain.model.Vehicle
import domain.model.VehicleCoverage
import domain.model.Warehouse

fun runGreedyFleetDemo(vehicles: List<Vehicle>) {
    val vehicleCoverages = vehicles.map { vehicle ->
        VehicleCoverage(
            vehicle = vehicle, coveredZones = setOf(vehicle.currentHub.regionalZone)
        )
    }

    runCatching {
        val dispatcher = GreedyFleetDispatcher()
        val targetZones = setOf(
            RegionalZone.NORTH,
            RegionalZone.CENTRAL,
            RegionalZone.WEST
        )

        val dispatchedVehicles = dispatcher.dispatch(targetZones = targetZones,
            vehicleCoverages = vehicleCoverages)

        val coveredZones = dispatchedVehicles.flatMap { it.coveredZones }.toSet()
        val uncoveredZones = targetZones - coveredZones

        println("\n[GREEDY FLEET DISPATCH]")
        println("Target zones: $targetZones")
        println("Selected vehicles: ${dispatchedVehicles.size}")
        println("Covered zones: $coveredZones")
        println("Uncovered zones: $uncoveredZones")
    }.onFailure { error ->
        println("\n[GREEDY FLEET DISPATCH ERROR]")
        println("Fleet dispatch failed: ${error.message}")
    }
}

fun runInvalidInputDemo() {
    runCatching {
        Warehouse(
            id = "invalid",
            name = "",
            regionalZone = RegionalZone.NORTH,
            latitude = 200.0,
            longitude = -300.0
        )
    }.onSuccess {
        println("\n[INVALID INPUT TEST]")
        println("Unexpected: invalid warehouse was accepted.")
    }.onFailure { error ->
        println("\n[INVALID INPUT TEST]")
        println("Invalid warehouse rejected successfully.")
        println("Validation details: ${error.message}")
    }
}
