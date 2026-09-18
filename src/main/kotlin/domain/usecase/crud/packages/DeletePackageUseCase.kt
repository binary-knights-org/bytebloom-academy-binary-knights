package domain.usecase.crud.packages

import domain.exception.EntityValidationException
import domain.repository.PackageRepository
import domain.validator.ValidationResult
import domain.validator.packages.PackageIdValidator

class DeletePackageUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: PackageIdValidator
) {
    suspend operator fun invoke(id: String): Boolean {
        val validation = idValidator.validate(id)

        if (validation is ValidationResult.Failure) {
            throw EntityValidationException(
                "Cannot delete package: invalid package ID."
            )
        }

        return packageRepository.delete(id)
    }
}
