package domain.usecase.search

import domain.model.Warehouse
import domain.repository.WarehouseRepository

class SearchWarehousesUseCase(
    warehouseRepository: WarehouseRepository
) : TextSearchUseCase<Warehouse>(
    loadAll = warehouseRepository::getAll,
    matches = { warehouse, query ->
        matchesAny(query, warehouse.id, warehouse.name, warehouse.regionalZone.name)
    }
)
