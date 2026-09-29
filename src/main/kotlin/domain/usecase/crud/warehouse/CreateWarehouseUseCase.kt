package domain.usecase.crud.warehouse

import domain.model.exception.OperationFailedException
import domain.model.exception.EntityValidationException
import domain.model.Warehouse
import domain.model.input.CreateWarehouseInput
import domain.repository.WarehouseRepository
import domain.validation.ValidationResult
import domain.validator.warehouse.CreateWarehouseValidator
import domain.model.RegionalZone

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: CreateWarehouseValidator
) {
    public suspend operator fun invoke(input: CreateWarehouseInput): Result<Warehouse> {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid)
            return Result.failure(EntityValidationException(validation.violations))

        return runCatching {
            Warehouse(
                id = input.id,
                name = input.name,
                regionalZone = RegionalZone.valueOf(input.regionalZone.trim().uppercase()),
                latitude = input.latitude,
                longitude = input.longitude
            )
        }.fold(
            onSuccess = { warehouse ->
                runCatching { warehouseRepository.create(warehouse) }
                    .fold(
                        onSuccess = { isCreated ->
                            if (isCreated) {
                                Result.success(warehouse)
                            } else {
                                Result.failure(OperationFailedException())
                            }
                        },
                        onFailure = { error ->
                            Result.failure(error)
                        }
                    )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
