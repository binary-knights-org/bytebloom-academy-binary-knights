package domain.usecase.crud.warehouse

import domain.model.RegionalZone
import domain.model.Warehouse
import domain.model.exception.EntityValidationException
import domain.model.input.CreateWarehouseInput
import domain.model.validation.ValidationResult
import domain.repository.WarehouseRepository
import domain.validator.warehouse.CreateWarehouseValidator

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: CreateWarehouseValidator
) {
    suspend operator fun invoke(input: CreateWarehouseInput): Result<Warehouse> {
        val validation = validator.validate(input)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                EntityValidationException(validation.violations)
            )
        }

        return runCatching {
            Warehouse(
                id = input.id,
                name = input.name,
                regionalZone = RegionalZone.valueOf(
                    input.regionalZone.trim().uppercase()
                ),
                latitude = input.latitude,
                longitude = input.longitude
            )
        }.mapCatching { warehouse ->
            warehouseRepository.create(warehouse)
            warehouse
        }
    }
}
