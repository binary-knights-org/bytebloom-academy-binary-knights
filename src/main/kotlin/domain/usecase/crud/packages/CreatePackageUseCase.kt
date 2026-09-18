package domain.usecase.crud.packages

import domain.exception.EntityValidationException
import domain.model.Package
import domain.repository.PackageRepository
import domain.validator.ValidationResult
import domain.validator.packages.CreatePackageValidator

class CreatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: CreatePackageValidator
) {
    suspend operator fun invoke(pkg: Package): Boolean {
        val validation = validator.validate(pkg)

        if (validation is ValidationResult.Failure) {
            throw EntityValidationException(
                "Cannot create package: invalid package data."
            )
        }

        return packageRepository.create(pkg)
    }
}
