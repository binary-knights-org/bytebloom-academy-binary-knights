package domain.model.input

import domain.model.Warehouse

data class UpdatePackageInput(
    val id: String,
    val weight: Double? = null,
    val priority: String? = null,
    val originHub: Warehouse? = null,
    val destinationHub: Warehouse? = null
)
