package domain.model

import domain.model.Warehouse

data class NetworkGraphContext(
    val warehouses: List<Warehouse>,
    val warehouseMap: Map<String, Warehouse>,
    val incomingOriginsByDestinationId: Map<String, List<String>>
)
