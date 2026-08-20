package domain.repository

interface PackageRepository {
    data class PackageRecord(
        val packageId: String,
        val weight: Double,
        val originHubId: String,
        val destinationHubId: String,
        val priority: String
    )

    fun getAllPackages(): List<PackageRecord>
}
