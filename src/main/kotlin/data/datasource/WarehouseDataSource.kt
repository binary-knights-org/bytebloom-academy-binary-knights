package data.datasource

import data.dataholder.WarehouseRaw

interface WarehouseDataSource {
    suspend fun getRawWarehouses(): List<WarehouseRaw>
    suspend fun createRawWarehouse(warehouse: WarehouseRaw): Boolean
    suspend fun updateRawWarehouse(id: String, warehouse: WarehouseRaw): Boolean
    suspend fun deleteRawWarehouse(id: String): Boolean
}
