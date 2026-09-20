package data.local.datasource

import data.local.dataholder.WarehouseRaw

interface CsvWarehouseDataSource {
    fun getAllWarehouses(): List<WarehouseRaw>
}
