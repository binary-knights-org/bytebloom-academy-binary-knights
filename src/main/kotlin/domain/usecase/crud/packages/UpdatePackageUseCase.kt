package domain.usecase.crud.packages

import domain.model.Package
import domain.model.input.UpdatePackageInput
import domain.repository.PackageRepository
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.packages.UpdatePackageValidator

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: UpdatePackageValidator
) {
    suspend operator fun invoke(input: UpdatePackageInput): ValidationResult {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid) return validation

        val existingPkg = packageRepository.getById(input.id)
            ?: return ValidationResult.Invalid(listOf(FieldViolation("id", "Package not found.")))

        val updatedPkg = Package.create(
            id = existingPkg.id,
            weight = input.weight ?: existingPkg.weight,
            priority = input.priority ?: existingPkg.priority,
            originHub = input.originHub ?: existingPkg.originHub,
            destinationHub = input.destinationHub ?: existingPkg.destinationHub
        )

        return if (packageRepository.update(updatedPkg)) ValidationResult.Valid
        else ValidationResult.Invalid(listOf(FieldViolation("database", "Failed to update package.")))
    }
}
