package domain.usecase.crud.packages

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.model.Package
import domain.model.input.CreatePackageInput
import domain.repository.PackageRepository
import domain.validator.packages.CreatePackageValidator

class CreatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: CreatePackageValidator
) {
    suspend operator fun invoke(input: CreatePackageInput): Result<Package> {
        val validation = validator.validate(input)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

        return runCatching {
            Package(
                id = input.id,
                weight = input.weight,
                priority = input.priority,
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
                            Result.failure(DatabaseConflictException("Failed to create package in database."))
                        }
                    },
                    onFailure = { error ->
                        Result.failure(DatabaseConflictException("Failed to create package: ${error.message}", error))
                    }
                )
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}
