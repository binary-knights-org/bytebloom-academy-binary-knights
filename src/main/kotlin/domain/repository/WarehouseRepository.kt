package domain.repository

import domain.model.Warehouse

interface WarehouseRepository {
   fun getAllWarehouses(): List<Warehouse>
}
