package domain.repository

interface VehicleRepository {
    data class VehicleRecord(
        val vehicleIds: List<String>,
        val currentHubId: String,
        val maxCapacityKg: Double,
        val costPerKm: Double
    )

    fun getAllVehicles(): List<VehicleRecord>
}
