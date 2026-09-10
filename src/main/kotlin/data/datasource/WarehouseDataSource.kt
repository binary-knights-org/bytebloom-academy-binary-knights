package data.datasource

import data.dataholder.WarehouseRaw

interface WarehouseDataSource {
    fun getRawWarehouses(): List<WarehouseRaw>
}
