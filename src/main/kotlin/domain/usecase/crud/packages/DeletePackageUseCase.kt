package domain.usecase.crud.packages

import domain.repository.PackageRepository
import domain.validator.PackageValidator
import domain.validator.ValidationResult

class DeletePackageUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        val isValid = PackageValidator.validateId(id) is ValidationResult.Success
        return isValid && packageRepository.delete(id)
    }
}
