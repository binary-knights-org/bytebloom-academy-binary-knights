package domain.usecase.crud.packages

import domain.model.Package
import domain.model.exception.EntityValidationException
import domain.model.exception.ResourceNotFoundException
import domain.model.input.UpdatePackageInput
import domain.model.toPriority
import domain.model.validation.ValidationResult
import domain.repository.PackageRepository
import domain.validator.packages.UpdatePackageValidator

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: UpdatePackageValidator
) {
    suspend operator fun invoke(input: UpdatePackageInput): Result<Package> {
        val validation = validator.validate(input)

        if (validation is ValidationResult.Invalid) {
            return Result.failure(
                EntityValidationException(validation.violations)
            )
        }

        return runCatching {
            val existing = packageRepository.getById(input.id)
                ?: throw ResourceNotFoundException()

            val updated = existing.copy(
                weight = input.weight ?: existing.weight,
                priority = input.priority?.toPriority() ?: existing.priority,
                originHub = input.originHub ?: existing.originHub,
                destinationHub = input.destinationHub ?: existing.destinationHub
            )

            packageRepository.update(updated)
            updated
        }
    }
}
