package domain.model.input

import domain.model.Warehouse

data class CreatePackageInput(
    val id: String,
    val weight: Double,
    val priority: String,
    val originHub: Warehouse,
    val destinationHub: Warehouse
)
