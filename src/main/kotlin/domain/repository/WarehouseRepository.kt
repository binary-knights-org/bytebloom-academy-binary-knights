package domain.repository

import domain.model.Warehouse
import domain.model.Package
import domain.model.Route
import domain.model.Vehicle

interface WarehouseRepository {
    fun getAllWarehouses(): List<Warehouse>
}
