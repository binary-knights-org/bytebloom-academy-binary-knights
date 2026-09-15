package domain.repository

import domain.model.Warehouse

interface WarehouseRepository : BaseRepository<Warehouse, String> {

   suspend fun getAllWarehouses(): List<Warehouse>
}
