package domain.usecase.crud.packages

import domain.exception.ResourceNotFoundException
import domain.model.input.UpdatePackageInput
import domain.repository.PackageRepository
import domain.validator.ValidationResult
import domain.validator.packages.UpdatePackageValidator

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: UpdatePackageValidator
) {
    suspend operator fun invoke(input: UpdatePackageInput): ValidationResult {
        val validation = validator.validate(input)
        if (validation.isInvalid) return validation

        val existingPackage = packageRepository.getById(input.id)
            ?: throw ResourceNotFoundException("Package with ID '${input.id}' was not found.")

        val updatedPackage = existingPackage.copy(
            weight = input.weight ?: existingPackage.weight,
            priority = input.priority ?: existingPackage.priority,
            originHub = input.originHub ?: existingPackage.originHub,
            destinationHub = input.destinationHub ?: existingPackage.destinationHub
        )

        packageRepository.update(updatedPackage)

        return ValidationResult.Valid
    }
}
