package domain.usecase.crud.packages

import domain.model.Package
import domain.model.input.UpdatePackageInput
import domain.repository.PackageRepository
import domain.exception.DatabaseOperationFailedException
import domain.exception.EntityNotFoundException
import domain.model.Warehouse
import domain.model.input.UpdateWarehouseInput
import domain.validator.ValidationResult
import domain.validator.packages.UpdatePackageValidator

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository, private val validator: UpdatePackageValidator
) {
    suspend operator fun invoke(input: UpdatePackageInput): ValidationResult<Package> {
        val validationResult = validator.validate(input)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        return executeUpdate(input)
    }

    private suspend fun executeUpdate(input: UpdatePackageInput): ValidationResult<Package> {
        val existingPkg = packageRepository.getById(input.id) ?: return ValidationResult.Failure(
            listOf(EntityNotFoundException("Package", input.id))
        )

        val updatedPkg = existingPkg.copy(
            weight = input.weight ?: existingPkg.weight,
            priority = input.priority ?: existingPkg.priority,
            originHub = input.originHub ?: existingPkg.originHub,
            destinationHub = input.destinationHub ?: existingPkg.destinationHub
        )

        val isUpdated = packageRepository.update(updatedPkg)

        return if (isUpdated) ValidationResult.Success(updatedPkg)
        else ValidationResult.Failure(listOf(DatabaseOperationFailedException("update", "package")))
    }
}
