package domain.usecase.crud.packages

import domain.exception.DatabaseConflictException
import domain.exception.EntityValidationException
import domain.repository.PackageRepository
import domain.validator.packages.PackageIdValidator

class DeletePackageUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: PackageIdValidator
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        val validation = idValidator.validate(id)
        if (validation.isInvalid) {
            return Result.failure(EntityValidationException(validation.errorsOrNull().orEmpty()))
        }

        return runCatching { packageRepository.delete(id) }.fold(
            onSuccess = { isDeleted ->
                if (isDeleted) {
                    Result.success(Unit)
                } else {
                    Result.failure(DatabaseConflictException("Failed to delete package with ID '$id' from database."))
                }
            },
            onFailure = { error ->
                Result.failure(DatabaseConflictException("Failed to delete package: ${error.message}", error))
            }
        )
    }
}
