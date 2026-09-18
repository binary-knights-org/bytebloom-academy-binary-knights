package domain.usecase.crud.packages

import domain.exception.EntityValidationException
import domain.model.Package
import domain.repository.PackageRepository
import domain.validator.ValidationResult
import domain.validator.packages.PackageIdValidator

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: PackageIdValidator
) {
    suspend operator fun invoke(id: String): Package? {
        val validation = idValidator.validate(id)

        if (validation is ValidationResult.Failure) {
            throw EntityValidationException(
                "Cannot get package: invalid package ID."
            )
        }

        return packageRepository.getById(id)
    }
}
