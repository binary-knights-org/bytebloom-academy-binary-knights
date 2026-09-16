package domain.usecase.crud.packages

import domain.model.Package
import domain.repository.PackageRepository
import domain.validator.PackageUpdateFields
import domain.validator.PackageValidator
import domain.validator.ValidationResult

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(pkg: Package): Boolean {
        val idValidation = PackageValidator.validateId(pkg.id)
        if (idValidation is ValidationResult.Failure) {
            return false
        }

        val fields = PackageUpdateFields(
            weight = pkg.weight,
            destinationHubId = pkg.destinationHub.id,
            priority = pkg.priority
        )

        val fieldsValidation = PackageValidator.validateForUpdate(fields)
        if (fieldsValidation is ValidationResult.Failure) {
            return false
        }

        return packageRepository.updatePackage(pkg)
    }
}
