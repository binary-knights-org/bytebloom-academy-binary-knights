package domain.builder

import domain.model.Warehouse

class DomainGraphBuilder(
    private val repositories: RepositoryProvider,
    private val domainGraph: DomainGraph = DomainGraph(repositories)
) {

    private val warehousesId: Map<String, Warehouse> = domainGraph.createWarehouseNodes()

    fun buildGraph(): List<Warehouse> {
        attachVehiclesToWarehouses()
        attachPackagesToWarehouses()
        attachRoutesToWarehouses()

        return warehousesId.values.toList()
    }

    private fun attachVehiclesToWarehouses() {
        domainGraph.attachVehiclesToWarehouses(warehousesId)
    }

    private fun attachPackagesToWarehouses() {
        domainGraph.attachPackagesToWarehouses(warehousesId)
    }

    private fun attachRoutesToWarehouses() {
        domainGraph.attachRoutesToWarehouses(warehousesId)
    }
}
