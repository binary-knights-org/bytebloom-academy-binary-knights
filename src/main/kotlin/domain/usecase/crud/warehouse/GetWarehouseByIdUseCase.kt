package domain.usecase.crud.warehouse

import domain.exception.EntityValidationException
import domain.exception.ResourceNotFoundException
import domain.model.Warehouse
import domain.repository.WarehouseRepository
import domain.validator.warehouse.ValidationResult
import domain.validator.ValidateWarehouseIdUseCase

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: ValidateWarehouseIdUseCase = ValidateWarehouseIdUseCase()
) {
    suspend operator fun invoke(warehouseId: String): Result<Warehouse> {
        val validation = idValidator(warehouseId)
        if (validation is ValidationResult.Invalid) {
            return Result.failure(EntityValidationException(validation.violations))
        }

        return runCatching {
            warehouseRepository.getById(warehouseId)
                ?: throw ResourceNotFoundException("Warehouse with ID '$warehouseId' not found")
        }
    }
}
