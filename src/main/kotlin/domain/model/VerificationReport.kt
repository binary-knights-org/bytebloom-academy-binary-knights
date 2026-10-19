package domain.model

data class VerificationReport(
    val migratedPackageIds: List<String>,
    val brokenVehicleId: String,
    val fallbackVehicleId: String,
    val validationError: List<String> = emptyList(),
)
