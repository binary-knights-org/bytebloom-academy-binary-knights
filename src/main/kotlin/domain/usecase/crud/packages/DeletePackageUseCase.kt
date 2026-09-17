package domain.usecase.crud.packages

import domain.repository.PackageRepository
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.packages.PackageIdValidator

class DeletePackageUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: PackageIdValidator
) {
    suspend operator fun invoke(id: String): ValidationResult {
        val validationResult = idValidator.validate(id)
        if (validationResult is ValidationResult.Invalid) {
            return validationResult
        }

        val isDeleted = packageRepository.delete(id)
        return if (isDeleted) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(
                listOf(FieldViolation("database", "Failed to delete package from database."))
            )
        }
    }
}
