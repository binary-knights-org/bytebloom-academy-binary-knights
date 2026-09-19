package domain.usecase.crud.packages

import domain.repository.PackageRepository
import domain.model.exception.DatabaseOperationFailedException
import domain.validator.ValidationResult
import domain.validator.packages.PackageIdValidator

class DeletePackageUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: PackageIdValidator
) {
    suspend operator fun invoke(id: String): ValidationResult<Unit> {
        val validationResult = idValidator.validate(id)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val isDeleted = packageRepository.delete(id)
        return if (isDeleted) {
            ValidationResult.Success(Unit)
        } else {
            ValidationResult.Failure(listOf(DatabaseOperationFailedException("delete", "package")))
        }
    }
}
