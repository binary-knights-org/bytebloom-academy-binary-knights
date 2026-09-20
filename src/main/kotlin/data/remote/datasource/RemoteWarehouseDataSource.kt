package data.remote.datasource

import data.local.dataholder.WarehouseRaw

interface RemoteWarehouseDataSource {
    suspend fun getRawWarehouses(): List<WarehouseRaw>
    suspend fun createRawWarehouse(warehouse: WarehouseRaw): Boolean
    suspend fun updateRawWarehouse(id: String, warehouse: WarehouseRaw): Boolean
    suspend fun deleteRawWarehouse(id: String): Boolean
}
