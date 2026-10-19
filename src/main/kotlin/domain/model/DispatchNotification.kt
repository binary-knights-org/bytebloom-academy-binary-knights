package domain.model

data class DispatchNotification(
    val packageId: String,
    val vehicleId: String,
    val message: String
)
