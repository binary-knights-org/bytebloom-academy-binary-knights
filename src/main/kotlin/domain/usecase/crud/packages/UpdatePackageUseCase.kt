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
        val fields = PackageUpdateFields(
            weight = pkg.weight,
            destinationHubId = pkg.destinationHub.id,
            priority = pkg.priority
        )

        val isValid = PackageValidator.validateId(pkg.id) is ValidationResult.Success &&
                PackageValidator.validateForUpdate(fields) is ValidationResult.Success

        return isValid && packageRepository.update(pkg)
    }
}
