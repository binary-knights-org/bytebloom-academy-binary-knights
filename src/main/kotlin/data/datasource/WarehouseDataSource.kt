package data.datasource

import data.dataholder.WarehouseRaw

interface WarehouseDataSource {
    suspend fun getRawWarehouses(): List<WarehouseRaw>
}
