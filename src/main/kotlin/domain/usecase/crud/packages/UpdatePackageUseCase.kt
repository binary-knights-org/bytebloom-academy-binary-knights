package domain.usecase.crud.packages

import domain.model.exception.EntityValidationException
import domain.model.exception.OperationFailedException
import domain.model.exception.ResourceNotFoundException
import data.exception.translateDataError
import domain.model.Package
import domain.model.input.UpdatePackageInput
import domain.model.toPriority
import domain.repository.PackageRepository
import domain.validator.ValidationResult
import domain.validator.packages.UpdatePackageValidator

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository, private val validator: UpdatePackageValidator
) {
    suspend operator fun invoke(input: UpdatePackageInput): Result<Package> {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid)
            return Result.failure(EntityValidationException(validation.violations))

        return runCatching { packageRepository.getById(input.id) }.fold(onSuccess = { existing ->
            if (existing == null) {
                Result.failure(ResourceNotFoundException())
            } else {
                executeUpdate(existing, input)
            }
        }, onFailure = { error -> Result.failure(translateDataError(error, "retrieve", "package")) })
    }

    private suspend fun executeUpdate(existing: Package, input: UpdatePackageInput): Result<Package> {
        return runCatching {
            existing.copy(
                weight = input.weight ?: existing.weight,
                priority = input.priority?.toPriority() ?: existing.priority,
                originHub = input.originHub ?: existing.originHub,
                destinationHub = input.destinationHub ?: existing.destinationHub
            )
        }.fold(onSuccess = { updatedPkg ->
            runCatching { packageRepository.update(updatedPkg) }.fold(onSuccess = { isUpdated ->
                if (isUpdated) {
                    Result.success(updatedPkg)
                } else {
                    Result.failure(OperationFailedException())
                }
            }, onFailure = { error -> Result.failure(translateDataError(error, "update", "package")) })
        }, onFailure = { error -> Result.failure(error) })
    }
}
