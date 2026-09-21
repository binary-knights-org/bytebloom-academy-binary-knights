package domain.usecase.crud.packages

import domain.model.exception.EntityValidationException
import domain.model.exception.OperationFailedException
import data.exception.translateDataError
import domain.model.Package
import domain.model.input.CreatePackageInput
import domain.model.toPriority
import domain.repository.PackageRepository
import domain.validator.ValidationResult
import domain.validator.packages.CreatePackageValidator

class CreatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: CreatePackageValidator
) {
    suspend operator fun invoke(input: CreatePackageInput): Result<Package> {
        val validation = validator.validate(input)
        if (validation is ValidationResult.Invalid)
            return Result.failure(EntityValidationException(validation.violations))

        return runCatching {
            Package(
                id = input.id,
                weight = input.weight,
                priority = input.priority.toPriority(),
                originHub = input.originHub,
                destinationHub = input.destinationHub
            )
        }.fold(
            onSuccess = { pkg ->
                runCatching { packageRepository.create(pkg) }.fold(
                    onSuccess = { isCreated ->
                        if (isCreated) {
                            Result.success(pkg)
                        } else {
                            Result.failure(OperationFailedException())
                        }
                    },
                    onFailure = { error -> Result.failure(translateDataError(error, "create", "package")) }
                )
            },
            onFailure = { error -> Result.failure(error) }
        )
    }
}
