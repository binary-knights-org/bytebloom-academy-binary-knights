package domain.usecase.crud.packages

import domain.model.Package
import domain.repository.PackageRepository

class CreatePackageUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(pkg: Package): Boolean{
        return packageRepository.create(pkg)
    }
}
