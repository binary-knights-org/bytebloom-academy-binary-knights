package domain.model

data class Package(
     val id: String,
     val weight: Double,
     val priority: String,
     val originHub: Warehouse,
     var destinationHub: Warehouse
)
