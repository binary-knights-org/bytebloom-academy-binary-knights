package domain.usecase.crud.warehouse

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.model.Warehouse
import domain.model.input.CreateWarehouseInput
import domain.repository.WarehouseRepository
import domain.validator.warehouse.CreateWarehouseValidator

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: CreateWarehouseValidator
) {
    suspend operator fun invoke(input: CreateWarehouseInput): Result<Warehouse> {
        val validation = validator.validate(input)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

        return runCatching {
            Warehouse(
                id = input.id,
                name = input.name,
                regionalZone = input.regionalZone,
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
                                Result.failure(DatabaseConflictException("Failed to create warehouse in database."))
                            }
                        },
                        onFailure = { error ->
                            Result.failure(
                                DatabaseConflictException(
                                    "Failed to create warehouse: ${error.message}", error))
                        }
                    )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
