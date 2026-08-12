package domain.repository

import data.dataholder.WarehouseRaw

interface WarehouseRepository {
   fun getAllWarehouses(): List<WarehouseRaw>
}
