package domain.usecase.crud.packages

import domain.model.Package
import domain.model.input.CreatePackageInput
import domain.repository.PackageRepository
import domain.validator.ValidationResult
import domain.validator.packages.CreatePackageValidator

class CreatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: CreatePackageValidator
) {
    suspend operator fun invoke(input: CreatePackageInput): ValidationResult {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid) return validation

        val newPackage = Package(
            id = input.id,
            weight = input.weight,
            priority = input.priority,
            originHub = input.originHub,
            destinationHub = input.destinationHub
        )

        packageRepository.create(newPackage)

        return ValidationResult.Valid
    }
}
