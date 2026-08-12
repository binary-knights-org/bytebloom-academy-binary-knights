package domain.repository

import data.dataholder.WarehouseRaw

interface WarehouseRepository {
   fun getWarehouses(): List<WarehouseRaw>
}