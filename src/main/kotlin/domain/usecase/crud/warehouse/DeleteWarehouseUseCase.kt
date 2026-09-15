package domain.usecase.crud.warehouse

import domain.repository.WarehouseRepository

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    suspend operator fun invoke(id:String):Boolean {
    return   warehouseRepository.deleteWarehouse(id)
    }
}