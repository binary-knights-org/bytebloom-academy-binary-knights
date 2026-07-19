package dataholders

    data class Package(
        val id: String,
        val weight: Double,
        val destinationHubId: String,
        val priority: String
    )

    data class Fleet(
        val vehicleId: String,
        val currentHubId: String,
        val maxCapacityKg: Double?,
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
