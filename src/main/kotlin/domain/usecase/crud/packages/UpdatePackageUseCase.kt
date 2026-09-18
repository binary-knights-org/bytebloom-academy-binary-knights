package domain.usecase.crud.packages

import domain.exception.EntityValidationException
import domain.model.Package
import domain.model.input.UpdatePackageInput
import domain.repository.PackageRepository
import domain.validator.ValidationResult
import domain.validator.packages.PackageIdValidator
import domain.validator.packages.UpdatePackageValidator

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: PackageIdValidator,
    private val validator: UpdatePackageValidator
) {
    suspend operator fun invoke(input: UpdatePackageInput): Boolean {
        validateInput(input)

        val existingPkg = packageRepository.getById(input.id)
            ?: throw EntityValidationException("Package with ID '${input.id}' was not found.")

        val updatedPkg = Package.create(
            id = existingPkg.id,
            weight = input.weight ?: existingPkg.weight,
            priority = input.priority ?: existingPkg.priority,
            originHub = input.originHub ?: existingPkg.originHub,
            destinationHub = input.destinationHub ?: existingPkg.destinationHub
        )

        return packageRepository.update(updatedPkg)
    }

    private fun validateInput(input: UpdatePackageInput) {
        if (idValidator.validate(input.id) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot update package: invalid package ID.")
        }

        if (validator.validate(input) is ValidationResult.Failure) {
            throw EntityValidationException("Cannot update package: invalid package data.")
        }
    }
}
