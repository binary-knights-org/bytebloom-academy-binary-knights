package domain.builder

import domain.model.Warehouse

class DomainGraphBuilder(
    private val repositories: RepositoryProvider,
    private val domainGraph: DomainGraph = DomainGraph(repositories)
) {

    private val warehousesId: Map<String, Warehouse> = domainGraph.createWarehouseNodes()

    fun buildGraph(): List<Warehouse> {
        domainGraph.attachVehiclesToWarehouses(warehousesId)
        domainGraph.attachPackagesToWarehouses(warehousesId)
        domainGraph.attachRoutesToWarehouses(warehousesId)

        return warehousesId.values.toList()
    }
}
