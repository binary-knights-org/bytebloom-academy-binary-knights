package domain.repository

import domain.model.Warehouse

interface WarehouseRepository : BaseRepository<Warehouse, String> {

   suspend fun getAllWarehouses(): List<Warehouse>
   suspend fun createWarehouse(warehouse: Warehouse): Boolean
   suspend fun getWarehouseById(id: String): Warehouse?
   suspend fun updateWarehouse(warehouse: Warehouse): Boolean
   suspend fun deleteWarehouse(id: String): Boolean
}
