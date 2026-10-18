package data.remote.datasource

import data.local.dataholder.WarehouseRaw

interface RemoteWarehouseDataSource {
    suspend fun getRawWarehouses(): List<WarehouseRaw>
    suspend fun createRawWarehouse(warehouse: WarehouseRaw): WarehouseRaw
    suspend fun updateRawWarehouse(id: String, warehouse: WarehouseRaw): WarehouseRaw
    suspend fun deleteRawWarehouse(id: String): Boolean
}
