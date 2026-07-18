package org.example.Task_01_Roba

    data class Package(
        val id: String,
        val weight: Double,
        val destinationHubId: String,
        val priority: String
    )

    data class Fleet(
        val vehicleId: String,
        val currentHubId: String,
        val maxCapacityKg: Double?, // Double? للسماح بالقيم المفقودة أو N/A
        val costPerKm: Double?
    )

    data class Route(
        val routeId: String,
        val originHubId: String,
        val destinationHubId: String,
        val distanceKm: Double,
        val typicalDelayMin: Int
    )

    data class Warehouse(
        val id: String,
        val name: String,
        val regionalZone: String
    )
