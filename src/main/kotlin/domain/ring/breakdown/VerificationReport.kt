package domain.ring.breakdown

data class VerificationReport(
    val migratedPackageIds: List<String>,
    val brokenVehicleId: String,
    val fallbackVehicleId: String
)
