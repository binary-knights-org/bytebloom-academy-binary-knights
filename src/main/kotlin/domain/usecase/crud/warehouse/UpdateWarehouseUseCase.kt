package domain.usecase.crud.warehouse

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.exception.ResourceNotFoundException
import domain.model.input.UpdateWarehouseInput
import domain.model.validation.ValidationResult
import domain.repository.WarehouseRepository
import domain.validator.warehouse.UpdateWarehouseValidator

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: UpdateWarehouseValidator
) {
    suspend operator fun invoke(input: UpdateWarehouseInput): Result<Warehouse> {
        val validation = validator.validate(input)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                EntityValidationException(validation.violations)
            )
        }

        return runCatching {
            val existing = warehouseRepository.getById(input.id)
                ?: throw ResourceNotFoundException()

            val updated = existing.copy(
                name = input.name ?: existing.name,
                regionalZone = input.regionalZone?.let {
                    RegionalZone.valueOf(it.trim().uppercase())
                } ?: existing.regionalZone,
                latitude = input.latitude ?: existing.latitude,
                longitude = input.longitude ?: existing.longitude
            )

            warehouseRepository.update(updated)
            updated
        }
    }
}
