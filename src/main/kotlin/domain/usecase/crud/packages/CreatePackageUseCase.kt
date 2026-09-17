package domain.usecase.crud.packages

import domain.model.Package
import domain.repository.PackageRepository
import domain.validator.PackageCreateFields
import domain.validator.PackageValidator
import domain.validator.ValidationResult

class CreatePackageUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(pkg: Package): Boolean {
        val fields = PackageCreateFields(
            packageId = pkg.id,
            weight = pkg.weight,
            originHubId = pkg.originHub.id,
            destinationHubId = pkg.destinationHub.id,
            priority = pkg.priority
        )

        val isValid = PackageValidator.validateForCreate(fields) is ValidationResult.Success
        return isValid && packageRepository.create(pkg)
    }
}
