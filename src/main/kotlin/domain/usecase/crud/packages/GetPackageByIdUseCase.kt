package domain.usecase.crud.packages

import domain.model.Package
import domain.repository.PackageRepository
import domain.validator.PackageValidator
import domain.validator.ValidationResult

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(id: String): Package? {
        val validationResult = PackageValidator.validateId(id)
        if (validationResult is ValidationResult.Failure) {
            return null
        }

        return packageRepository.getPackageById(id)
    }
}
