package domain.usecase.crud.packages

import domain.repository.PackageRepository
import domain.validator.PackageValidator
import domain.validator.ValidationResult

class DeletePackageUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(id: String): Boolean{
        val validationResult = PackageValidator.validateId(id)
        if (validationResult is ValidationResult.Failure) {
            return false
        }

        return packageRepository.deletePackage(id)
    }
}
