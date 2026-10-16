package data.local.datasource

import data.local.dataholder.WarehouseRaw

interface CsvWarehouseDataSource {
    suspend fun getAllWarehouses(): List<WarehouseRaw>
}
