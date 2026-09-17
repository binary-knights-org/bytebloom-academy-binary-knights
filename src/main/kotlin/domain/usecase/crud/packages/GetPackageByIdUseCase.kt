package domain.usecase.crud.packages

import domain.model.Package
import domain.repository.PackageRepository
import domain.validator.FieldViolation
import domain.validator.ValidationResult
import domain.validator.packages.PackageIdValidator

sealed interface GetPackageResult {
    data class Success(val pkg: Package) : GetPackageResult
    data class Failure(val violations: List<FieldViolation>) : GetPackageResult
}

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: PackageIdValidator
) {
    suspend operator fun invoke(id: String): GetPackageResult {
        val validationResult = idValidator.validate(id)
        if (validationResult is ValidationResult.Invalid) {
            return GetPackageResult.Failure(validationResult.violations)
        }

        val pkg = packageRepository.getById(id)
        return if (pkg != null) {
            GetPackageResult.Success(pkg)
        } else {
            GetPackageResult.Failure(
                listOf(FieldViolation("id", "Package with ID '$id' was not found."))
            )
        }
    }
}
