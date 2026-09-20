package domain.usecase.crud.packages

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.exception.ResourceNotFoundException
import domain.model.Package
import domain.model.input.UpdatePackageInput
import domain.repository.PackageRepository
import domain.validator.packages.UpdatePackageValidator

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: UpdatePackageValidator
) {
    suspend operator fun invoke(input: UpdatePackageInput): Result<Package> {
        val validation = validator.validate(input)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

        return runCatching { packageRepository.getById(input.id) }.fold(
            onSuccess = { existing ->
                if (existing == null) {
                    Result.failure(ResourceNotFoundException("Package with ID '${input.id}' was not found."))
                } else {
                    executeUpdate(existing, input)
                }
            },
            onFailure = { error ->
                Result.failure(
                    DatabaseConflictException(
                        "Failed to retrieve package for update: ${error.message}", error))
            }
        )
    }

    private suspend fun executeUpdate(existing: Package, input: UpdatePackageInput): Result<Package> {
        return runCatching {
            existing.copy(
                weight = input.weight ?: existing.weight,
                priority = input.priority ?: existing.priority,
                originHub = input.originHub ?: existing.originHub,
                destinationHub = input.destinationHub ?: existing.destinationHub
            )
        }.fold(
            onSuccess = { updatedPkg ->
                runCatching { packageRepository.update(updatedPkg) }.fold(
                    onSuccess = { isUpdated ->
                        if (isUpdated) {
                            Result.success(updatedPkg)
                        } else {
                            Result.failure(DatabaseConflictException("Failed to update package in database."))
                        }
                    },
                    onFailure = { error ->
                        Result.failure(DatabaseConflictException("Failed to update package: ${error.message}", error))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
