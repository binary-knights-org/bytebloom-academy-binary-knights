package domain.usecase.crud.packages

import domain.model.Package
import domain.repository.PackageRepository
import domain.exception.EntityNotFoundException
import domain.validator.ValidationResult
import domain.validator.packages.PackageIdValidator

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: PackageIdValidator
) {
    suspend operator fun invoke(id: String): ValidationResult<Package> {
        val validationResult = idValidator.validate(id)
        if (validationResult is ValidationResult.Failure) {
            return validationResult
        }

        val pkg = packageRepository.getById(id)
        return if (pkg != null) {
            ValidationResult.Success(pkg)
        } else {
            ValidationResult.Failure(listOf(EntityNotFoundException("Package", id)))
        }
    }
}
