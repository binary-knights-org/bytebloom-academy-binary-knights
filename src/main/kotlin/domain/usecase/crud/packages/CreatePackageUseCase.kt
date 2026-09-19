package domain.usecase.crud.packages

import domain.model.Package
import domain.model.input.CreatePackageInput
import domain.repository.PackageRepository
import domain.exception.DatabaseOperationFailedException
import domain.validator.ValidationResult
import domain.validator.packages.CreatePackageValidator

class CreatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: CreatePackageValidator
) {
    suspend operator fun invoke(input: CreatePackageInput): ValidationResult<Package> {
        val validationResult = validator.validate(input)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val pkg = Package(
            id = input.id,
            weight = input.weight,
            priority = input.priority,
            originHub = input.originHub,
            destinationHub = input.destinationHub
        )

        val isCreated = packageRepository.create(pkg)

        return if (isCreated) {
            ValidationResult.Success(pkg)
        } else {
            ValidationResult.Failure(listOf(DatabaseOperationFailedException("create", "package")))
        }
    }
}
